package com.growthfusion.controller;

import com.growthfusion.dto.CampaignPerformanceDto;
import com.growthfusion.service.CampaignService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing:
 * GET /api/v1/campaigns/active?date=YYYY-MM-DD
 * Delegates all business logic to CampaignService.
 */
@RestController
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveCampaigns(@RequestParam(required = true) String date) {
        try {
            List<CampaignPerformanceDto> result = campaignService.getActiveCampaignPerformance(date);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("Invalid date format", "Expected YYYY-MM-DD")
            );
        }
    }

    // Simple error response wrapper
    record ErrorResponse(String error, String message) {}
}
