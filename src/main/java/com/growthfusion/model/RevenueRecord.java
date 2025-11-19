package com.growthfusion.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a single revenue row aggregated by ingested_at (UTC).
 * Used for summing revenue/clicks/conversions/lp metrics.
 */
@Data
public class RevenueRecord {
    private String campaignName;
    private Long clicks;
    private Long uniqueClicks;
    private Long conversions;
    private Long lpViews;
    private Long lpClicks;
    private Double revenue;
    private LocalDateTime ingestedAt;
}
