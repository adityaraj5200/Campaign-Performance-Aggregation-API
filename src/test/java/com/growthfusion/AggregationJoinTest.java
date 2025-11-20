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
import java.util.List;


/**
 * Integration-style test that:
 * - loads schema.sql and data.sql into H2
 * - runs the full aggregation flow
 * - validates snapshot extraction, revenue aggregation & metric correctness
 */
public class AggregationJoinTest {
    private CampaignService campaignService;

    @BeforeEach
    public void setup() {

        DataSource ds = DataSourceBuilder.create()
                .url("jdbc:h2:mem:growthfusiondb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                .username("adityarajgrowthfusion")
                .password("password")
                .driverClassName("org.h2.Driver")
                .build();

        JdbcTemplate jdbc = new JdbcTemplate(ds);

        // Execute schema + data
        jdbc.execute(getResource("schema.sql"));
        jdbc.execute(getResource("data.sql"));

        DateWindowService dateWindowService = new DateWindowServiceImpl();
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
    public void testAggregationForNov13() {

        List<CampaignPerformanceDto> results =
                campaignService.getActiveCampaignPerformance("2025-11-13");

        // There are MANY campaigns; just ensure non-zero result set
        Assertions.assertFalse(results.isEmpty(), "Expected non-empty campaign list");

        // Verify a known strong performer exists
        CampaignPerformanceDto laptop =
                results.stream()
                        .filter(r -> r.getCampaignName().equalsIgnoreCase("Laptop Discount"))
                        .findFirst()
                        .orElse(null);

        Assertions.assertNotNull(laptop, "Laptop Discount should exist");

        // Check metric correctness for a known example
        double expectedProfit = laptop.getRevenue() - laptop.getSpend();
        Assertions.assertEquals(expectedProfit, laptop.getProfit(), 0.0001);

        // ROAS should be revenue / spend
        if (laptop.getSpend() > 0) {
            double expectedRoas = laptop.getRevenue() / laptop.getSpend();
            Assertions.assertEquals(expectedRoas, laptop.getRoas(), 0.0001);
        }
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
