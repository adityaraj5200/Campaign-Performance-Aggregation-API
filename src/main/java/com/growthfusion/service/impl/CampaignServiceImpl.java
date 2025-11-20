package com.growthfusion.service.impl;

import com.growthfusion.dto.CampaignPerformanceDto;
import com.growthfusion.dto.CostSnapshotDto;
import com.growthfusion.dto.DateWindowDto;
import com.growthfusion.model.CostRow;
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

        long start = System.currentTimeMillis();
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

            // Extract cost snapshots (unified logic)
            List<CostSnapshotDto> allSnapshots = new ArrayList<>();
            allSnapshots.addAll(extractLatestSnapshots(metaRows, "meta"));
            allSnapshots.addAll(extractLatestSnapshots(snapRows, "snapchat"));

            // Aggregate revenue
            Map<String, RevenueRecord> revenueAgg = aggregateRevenue(revenueRows);

            // Merge cost + revenue
            List<CampaignPerformanceDto> results = new ArrayList<>();

            for (CostSnapshotDto cost : allSnapshots) {
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

            // Sort by profit desc, spend desc
            results.sort(
                    Comparator.comparing(CampaignPerformanceDto::getProfit).reversed()
                            .thenComparing(CampaignPerformanceDto::getSpend).reversed()
            );

            log.debug("Returning {} campaigns", results.size());

            long end = System.currentTimeMillis();
            log.debug("Aggregation completed in {} ms", (end - start));

            return results;

        } catch (Exception ex) {
            log.error("Error during campaign aggregation: {}", ex.getMessage(), ex);
            throw ex; // Let GlobalExceptionHandler handle
        }
    }

    private List<CostSnapshotDto> extractLatestSnapshots(
            List<? extends CostRow> rows,
            String platform
    ) {
        Map<String, List<CostRow>> groups =
                rows.stream()
                        .collect(Collectors.groupingBy(
                                r -> CampaignNameNormalizer.normalize(r.getCampaignName())
                        ));

        List<CostSnapshotDto> result = new ArrayList<>();

        for (List<CostRow> group : groups.values()) {

            // ACTIVE candidates
            List<CostRow> active = group.stream()
                    .filter(r ->
                            "ACTIVE".equalsIgnoreCase(r.getStatus())
                                    || (r.getStatus() == null && r.getSpend() > 0)
                    )
                    .toList();

            if (active.isEmpty()) continue;

            // Pick latest by event_time_la
            CostRow latest = active.stream()
                    .max(Comparator.comparing(CostRow::getEventTimeLa))
                    .get();

            // LA -> UTC
            LocalDateTime utc = latest.getEventTimeLa()
                    .atZone(java.time.ZoneId.of("America/Los_Angeles"))
                    .withZoneSameInstant(java.time.ZoneId.of("UTC"))
                    .toLocalDateTime();

            // Create snapshot
            result.add(new CostSnapshotDto(
                    platform,
                    latest.getCampaignName(),
                    latest.getStatus(),
                    latest.getSpend(),
                    latest.getImpressions(),
                    utc
            ));
        }

        return result;
    }

    private Map<String, RevenueRecord> aggregateRevenue(List<RevenueRecord> rows) {

        Map<String, RevenueRecord> agg = new HashMap<>();

        for (RevenueRecord r : rows) {
            String normalized = CampaignNameNormalizer.normalize(r.getCampaignName());

            agg.compute(normalized, (k, existing) -> {
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
