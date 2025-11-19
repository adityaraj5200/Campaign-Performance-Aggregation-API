package com.growthfusion.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a single cost entry from the Meta ads cost table.
 * Used for extracting latest spend/impressions snapshot within the LA day.
 */
@Data
public class MetaCost {
    private String campaignId;
    private String campaignName;
    private String status;
    private Double spend;
    private Long impressions;
    private LocalDateTime eventTimeLa;
    private LocalDateTime ingestedAt;
}
