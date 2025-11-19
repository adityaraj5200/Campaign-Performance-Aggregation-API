package com.growthfusion.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO returned by the API after merging cost + revenue and computing metrics.
 * Represents a single campaign's unified performance output.
 */
@Data
public class CampaignPerformanceDto {

    private String platform;
    private String campaignName;
    private String status;
    private LocalDateTime lastCostEventTimeUtc;

    private Double spend;
    private Long impressions;

    private Double revenue;
    private Long clicks;
    private Long uniqueClicks;
    private Long conversions;
    private Long lpViews;
    private Long lpClicks;

    private Double profit;
    private Double roas;
    private Double roi;
    private Double lpctr;
    private Double epc;
    private Double lpcpc;
}
