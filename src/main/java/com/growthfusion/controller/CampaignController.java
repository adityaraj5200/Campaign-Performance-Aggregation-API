package com.growthfusion.controller;

import com.growthfusion.dto.CampaignPerformanceDto;
import com.growthfusion.service.CampaignService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller exposing:
 * GET /api/v1/campaigns/active?date=YYYY-MM-DD
 * All business logic handled by CampaignService.
 */
@RestController
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveCampaigns(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);  // will throw DateTimeParseException if invalid
        List<CampaignPerformanceDto> result =
                campaignService.getActiveCampaignPerformance(localDate);

        return ResponseEntity.ok(result);
    }
}
