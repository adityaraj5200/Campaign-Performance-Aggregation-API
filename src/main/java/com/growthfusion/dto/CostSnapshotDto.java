package com.growthfusion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents the latest cost snapshot (spend + impressions + status)
 * for a campaign within the LA day window.
 */
@Data
@AllArgsConstructor
public class CostSnapshotDto {
    private String platform;
    private String campaignName;
    private String status;
    private Double spend;
    private Long impressions;
    private LocalDateTime lastEventTimeUtc;
}
