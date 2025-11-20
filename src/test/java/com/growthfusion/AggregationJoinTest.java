package com.growthfusion;

import com.growthfusion.dto.CampaignPerformanceDto;
import com.growthfusion.repository.MetaCostRepository;
import com.growthfusion.repository.RevenueRepository;
import com.growthfusion.repository.SnapchatCostRepository;
import com.growthfusion.service.CampaignService;
import com.growthfusion.service.DateWindowService;
import com.growthfusion.service.impl.CampaignServiceImpl;
import com.growthfusion.service.impl.DateWindowServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Integration-style test that:
 * - loads schema.sql and data.sql into H2
 * - runs the full aggregation flow
 * - validates snapshot extraction, revenue aggregation & metric correctness
 */
public class AggregationJoinTest {
    private CampaignService campaignService;
    private JdbcTemplate jdbc;
    private DateWindowService dateWindowService;

    @BeforeEach
    public void setup() {

        DataSource ds = DataSourceBuilder.create()
                .url("jdbc:h2:mem:growthfusiondb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                .username("adityarajgrowthfusion")
                .password("password")
                .driverClassName("org.h2.Driver")
                .build();

        this.jdbc = new JdbcTemplate(ds);

        // Execute schema + data
        jdbc.execute(getResource("schema.sql"));
        jdbc.execute(getResource("data.sql"));

        this.dateWindowService = new DateWindowServiceImpl();
        MetaCostRepository metaRepo = new MetaCostRepository(jdbc);
        SnapchatCostRepository snapRepo = new SnapchatCostRepository(jdbc);
        RevenueRepository revenueRepo = new RevenueRepository(jdbc);

        this.campaignService = new CampaignServiceImpl(
                dateWindowService,
                metaRepo,
                snapRepo,
                revenueRepo
        );
    }

    @Test
    public void testAggregationForNov13_basicAssertions() {

        List<CampaignPerformanceDto> results =
                campaignService.getActiveCampaignPerformance(LocalDate.parse("2025-11-13"));

        // There should be exactly 5 normalized campaigns (as per updated data.sql)
        Assertions.assertEquals(5, results.size(), "Expected 5 unique campaigns after normalization");

        // Verify a known campaign exists (Fitness Tracker Ads)
        CampaignPerformanceDto fitness =
                results.stream()
                        .filter(r -> r.getCampaignName().equalsIgnoreCase("Fitness Tracker Ads"))
                        .findFirst()
                        .orElse(null);

        Assertions.assertNotNull(fitness, "Fitness Tracker Ads should exist in results");

        // Profit correctness: profit == revenue - spend
        double expectedProfit = fitness.getRevenue() - fitness.getSpend();
        Assertions.assertEquals(expectedProfit, fitness.getProfit(), 0.0001);

        // ROAS correctness (if spend > 0)
        if (fitness.getSpend() > 0) {
            double expectedRoas = fitness.getRevenue() / fitness.getSpend();
            Assertions.assertEquals(expectedRoas, fitness.getRoas(), 0.0001);
        }
    }

    @Test
    public void testRevenueAggregation_matchesDatabaseSum() {

        LocalDate date = LocalDate.parse("2025-11-13");
        var window = dateWindowService.computeUtcWindow(date);
        Timestamp utcStart = Timestamp.valueOf(window.getUtcStart());
        Timestamp utcEnd = Timestamp.valueOf(window.getUtcEnd());

        // Pick "Smart Home Bundle" and ensure service's revenue equals DB sum (case-insensitive)
        String campaignName = "Smart Home Bundle";

        Double dbSum = jdbc.queryForObject(
                "SELECT SUM(revenue) FROM revenue_table WHERE ingested_at BETWEEN ? AND ? AND LOWER(campaign_name) = LOWER(?)",
                new Object[]{utcStart, utcEnd, campaignName},
                Double.class
        );
        if (dbSum == null) dbSum = 0.0;

        List<CampaignPerformanceDto> results =
                campaignService.getActiveCampaignPerformance(date);

        CampaignPerformanceDto dto =
                results.stream()
                        .filter(r -> r.getCampaignName().equalsIgnoreCase(campaignName))
                        .findFirst()
                        .orElse(null);

        Assertions.assertNotNull(dto, "Smart Home Bundle result must be present");

        // Service's revenue should match DB aggregated revenue
        Assertions.assertEquals(dbSum, dto.getRevenue(), 0.0001);
    }

    @Test
    public void testNormalization_and_combinedRevenueForOrganicCoffee() {

        LocalDate date = LocalDate.parse("2025-11-13");
        var window = dateWindowService.computeUtcWindow(date);
        Timestamp utcStart = Timestamp.valueOf(window.getUtcStart());
        Timestamp utcEnd = Timestamp.valueOf(window.getUtcEnd());

        // Sum revenue rows for all variants of "Organic Coffee Launch" within the UTC window
        Double dbSum = jdbc.queryForObject(
                "SELECT SUM(revenue) FROM revenue_table WHERE ingested_at BETWEEN ? AND ? AND LOWER(campaign_name) LIKE ?",
                new Object[]{utcStart, utcEnd, "%organic%coffee%"},
                Double.class
        );
        if (dbSum == null) dbSum = 0.0;

        List<CampaignPerformanceDto> results =
                campaignService.getActiveCampaignPerformance(date);

        CampaignPerformanceDto dto =
                results.stream()
                        .filter(r -> r.getCampaignName().toLowerCase(Locale.ROOT).contains("organic"))
                        .findFirst()
                        .orElse(null);

        Assertions.assertNotNull(dto, "Organic Coffee Launch should be present after normalization");
        Assertions.assertEquals(dbSum, dto.getRevenue(), 0.0001);
    }

    // -----------------------------
    // Helper to load SQL from resources
    // -----------------------------
    private String getResource(String filename) {
        try {
            return new String(
                getClass().getClassLoader().getResourceAsStream(filename).readAllBytes()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + filename, e);
        }
    }
}
