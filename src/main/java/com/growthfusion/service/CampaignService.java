package com.growthfusion.service;

import com.growthfusion.dto.CampaignPerformanceDto;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface defining the contract for:
 * - LA → UTC window conversion
 * - fetching cost + revenue
 * - merging, aggregating, and computing metrics
 */
public interface CampaignService {

    List<CampaignPerformanceDto> getActiveCampaignPerformance(LocalDate localDate);
}
