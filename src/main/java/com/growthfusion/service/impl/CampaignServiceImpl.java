package com.growthfusion.service.impl;

import com.growthfusion.dto.CampaignPerformanceDto;
import com.growthfusion.dto.CostSnapshotDto;
import com.growthfusion.dto.DateWindowDto;
import com.growthfusion.model.MetaCost;
import com.growthfusion.model.SnapchatCost;
import com.growthfusion.model.RevenueRecord;
import com.growthfusion.repository.MetaCostRepository;
import com.growthfusion.repository.SnapchatCostRepository;
import com.growthfusion.repository.RevenueRepository;
import com.growthfusion.service.CampaignService;
import com.growthfusion.service.DateWindowService;
import com.growthfusion.util.CampaignNameNormalizer;
import com.growthfusion.util.MetricsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main service implementing:
 * 1) LA → UTC window conversion
 * 2) Loading cost + revenue
 * 3) Extracting latest snapshot per campaign
 * 4) Filtering active campaigns
 * 5) Joining with revenue
 * 6) Computing metrics
 * 7) Sorting final output
 */
@Slf4j
@Service
public class CampaignServiceImpl implements CampaignService {

    private final DateWindowService dateWindowService;
    private final MetaCostRepository metaCostRepository;
    private final SnapchatCostRepository snapchatCostRepository;
    private final RevenueRepository revenueRepository;

    public CampaignServiceImpl(DateWindowService dateWindowService,
                               MetaCostRepository metaCostRepository,
                               SnapchatCostRepository snapchatCostRepository,
                               RevenueRepository revenueRepository) {
        this.dateWindowService = dateWindowService;
        this.metaCostRepository = metaCostRepository;
        this.snapchatCostRepository = snapchatCostRepository;
        this.revenueRepository = revenueRepository;
    }

    @Override
    public List<CampaignPerformanceDto> getActiveCampaignPerformance(LocalDate localDate) {

        long startTime = System.currentTimeMillis();


        try {
            // Compute UTC window
            DateWindowDto window = dateWindowService.computeUtcWindow(localDate);
            LocalDateTime utcStart = window.getUtcStart();
            LocalDateTime utcEnd = window.getUtcEnd();

            log.debug("Query UTC Window: start={}, end={}", utcStart, utcEnd);

            // Fetch all rows within UTC window
            List<MetaCost> metaRows = metaCostRepository.findByUtcWindow(utcStart, utcEnd);
            List<SnapchatCost> snapRows = snapchatCostRepository.findByUtcWindow(utcStart, utcEnd);
            List<RevenueRecord> revenueRows = revenueRepository.findByUtcWindow(utcStart, utcEnd);

            log.debug("Fetched Meta rows = {}", metaRows.size());
            log.debug("Fetched Snapchat rows = {}", snapRows.size());
            log.debug("Fetched Revenue rows = {}", revenueRows.size());

            // Normalize cost lists into snapshot maps
            Map<String, CostSnapshotDto> metaLatestActiveCostSnapshots = computeLatestActiveCostSnapshots(metaRows, "meta");

            Map<String, CostSnapshotDto> snapchatLatestActiveCostSnapshots = computeLatestActiveCostSnapshots(snapRows, "snapchat");

            // Combine maps
            Map<String, CostSnapshotDto> allLatestActiveCostSnapshots = new HashMap<>();
            allLatestActiveCostSnapshots.putAll(metaLatestActiveCostSnapshots);
            allLatestActiveCostSnapshots.putAll(snapchatLatestActiveCostSnapshots);

            log.debug("Active campaign snapshots = {}", allLatestActiveCostSnapshots.size());

            // Aggregate revenue by normalized campaign name
            Map<String, RevenueRecord> revenueAgg = aggregateRevenue(revenueRows);

            // Merge cost + revenue + compute metrics
            List<CampaignPerformanceDto> results = new ArrayList<>();
            for (CostSnapshotDto cost : allLatestActiveCostSnapshots.values()) {

                String normalized = CampaignNameNormalizer.normalize(cost.getCampaignName());
                RevenueRecord rev = revenueAgg.getOrDefault(normalized, emptyRevenue());

                CampaignPerformanceDto dto = new CampaignPerformanceDto();
                dto.setPlatform(cost.getPlatform());
                dto.setCampaignName(cost.getCampaignName());
                dto.setStatus(cost.getStatus());
                dto.setLastCostEventTimeUtc(cost.getLastEventTimeUtc());

                dto.setSpend(cost.getSpend());
                dto.setImpressions(cost.getImpressions());

                dto.setRevenue(rev.getRevenue());
                dto.setClicks(rev.getClicks());
                dto.setUniqueClicks(rev.getUniqueClicks());
                dto.setConversions(rev.getConversions());
                dto.setLpViews(rev.getLpViews());
                dto.setLpClicks(rev.getLpClicks());

                // Metrics
                dto.setProfit(MetricsUtil.profit(dto.getRevenue(), dto.getSpend()));
                dto.setRoas(MetricsUtil.roas(dto.getRevenue(), dto.getSpend()));
                dto.setRoi(MetricsUtil.roi(dto.getRevenue(), dto.getSpend()));
                dto.setLpctr(MetricsUtil.lpctr(dto.getLpClicks(), dto.getLpViews()));
                dto.setEpc(MetricsUtil.epc(dto.getRevenue(), dto.getLpClicks()));
                dto.setLpcpc(MetricsUtil.lpcpc(dto.getSpend(), dto.getLpClicks()));

                results.add(dto);
            }

            // Sort: profit desc, spend desc
            results.sort(Comparator
                    .comparing(CampaignPerformanceDto::getProfit).reversed()
                    .thenComparing(CampaignPerformanceDto::getSpend).reversed());

            log.debug("Returning {} campaigns", results.size());

            long endTime = System.currentTimeMillis();
            log.debug("Campaign aggregation completed in {} ms", (endTime - startTime));

            return results;

        } catch (Exception ex) {
            log.error("Error during campaign aggregation: {}", ex.getMessage(), ex);
            throw ex; // Let GlobalExceptionHandler handle
        }
    }


    private Map<String, CostSnapshotDto> computeLatestActiveCostSnapshots(List<?> rows, String platform) {

        Map<String, CostSnapshotDto> map = new HashMap<>();

        Map<String, List<Object>> groups = rows.stream()
                .collect(Collectors.groupingBy(r -> {
                    String name = (r instanceof MetaCost) ?
                            ((MetaCost) r).getCampaignName() :
                            ((SnapchatCost) r).getCampaignName();
                    return CampaignNameNormalizer.normalize(name);
                }));

        for (var entry : groups.entrySet()) {
            String norm = entry.getKey();
            List<Object> list = entry.getValue();

            // Filter ACTIVE logic: status=ACTIVE OR (status NULL AND spend>0)
            List<Object> activeCandidates = list.stream()
                    .filter(r -> {
                        String status;
                        Double spend;

                        if (r instanceof MetaCost m) {
                            status = m.getStatus();
                            spend = m.getSpend();
                        } else {
                            SnapchatCost s = (SnapchatCost) r;
                            status = s.getStatus();
                            spend = s.getSpend();
                        }

                        return ("ACTIVE".equalsIgnoreCase(status)) ||
                                (status == null && spend != null && spend > 0);
                    })
                    .toList();

            if (activeCandidates.isEmpty()) continue;

            // Pick latest by event_time_la
            Object latest = activeCandidates.stream()
                    .max(Comparator.comparing(o ->
                            (o instanceof MetaCost m)
                                    ? m.getEventTimeLa()
                                    : ((SnapchatCost) o).getEventTimeLa()))
                    .get();

            String campaignName;
            String status;
            Double spend;
            Long impressions;
            LocalDateTime eventTimeLa;

            if (latest instanceof MetaCost m) {
                campaignName = m.getCampaignName();
                status = m.getStatus();
                spend = m.getSpend();
                impressions = m.getImpressions();
                eventTimeLa = m.getEventTimeLa();
            } else {
                SnapchatCost s = (SnapchatCost) latest;
                campaignName = s.getCampaignName();
                status = s.getStatus();
                spend = s.getSpend();
                impressions = s.getImpressions();
                eventTimeLa = s.getEventTimeLa();
            }

            // Convert LA → UTC
            LocalDateTime eventTimeUtc = eventTimeLa
                    .atZone(java.time.ZoneId.of("America/Los_Angeles"))
                    .withZoneSameInstant(java.time.ZoneId.of("UTC"))
                    .toLocalDateTime();

            map.put(norm, new CostSnapshotDto(
                    platform,
                    campaignName,
                    status,
                    spend,
                    impressions,
                    eventTimeUtc
            ));
        }

        return map;
    }


    private Map<String, RevenueRecord> aggregateRevenue(List<RevenueRecord> rows) {

        Map<String, RevenueRecord> agg = new HashMap<>();

        for (RevenueRecord r : rows) {
            String norm = CampaignNameNormalizer.normalize(r.getCampaignName());

            agg.compute(norm, (k, existing) -> {
                if (existing == null) return cloneRevenue(r);

                existing.setRevenue(existing.getRevenue() + r.getRevenue());
                existing.setClicks(existing.getClicks() + r.getClicks());
                existing.setUniqueClicks(existing.getUniqueClicks() + r.getUniqueClicks());
                existing.setConversions(existing.getConversions() + r.getConversions());
                existing.setLpViews(existing.getLpViews() + r.getLpViews());
                existing.setLpClicks(existing.getLpClicks() + r.getLpClicks());
                return existing;
            });
        }

        return agg;
    }

    private RevenueRecord emptyRevenue() {
        RevenueRecord r = new RevenueRecord();
        r.setRevenue(0.0);
        r.setClicks(0L);
        r.setUniqueClicks(0L);
        r.setConversions(0L);
        r.setLpViews(0L);
        r.setLpClicks(0L);
        return r;
    }

    private RevenueRecord cloneRevenue(RevenueRecord r) {
        RevenueRecord c = new RevenueRecord();
        c.setCampaignName(r.getCampaignName());
        c.setRevenue(r.getRevenue());
        c.setClicks(r.getClicks());
        c.setUniqueClicks(r.getUniqueClicks());
        c.setConversions(r.getConversions());
        c.setLpViews(r.getLpViews());
        c.setLpClicks(r.getLpClicks());
        c.setIngestedAt(r.getIngestedAt());
        return c;
    }
}
