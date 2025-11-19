package com.growthfusion.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a single cost entry from the Snapchat ads cost table.
 * Used for latest-snapshot extraction and active-campaign evaluation.
 */
@Data
public class SnapchatCost {
    private String campaignId;
    private String campaignName;
    private String status;
    private Double spend;
    private Long impressions;
    private LocalDateTime eventTimeLa;
    private LocalDateTime ingestedAt;
}
