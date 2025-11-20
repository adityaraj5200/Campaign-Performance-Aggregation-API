package com.growthfusion;

import com.growthfusion.controller.CampaignController;
import com.growthfusion.service.CampaignService;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(controllers = CampaignController.class)
public class InvalidDateFormatTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CampaignService campaignService;

    // 1. Wrong delimiter (DD-MM-YYYY)
    @Test
    public void testInvalidDateFormat_DDMMYYYY() throws Exception {
        mockMvc.perform(get("/api/v1/campaigns/active")
                        .param("date", "13-11-2025"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid date format"))
                .andExpect(jsonPath("$.message").value("Expected YYYY-MM-DD"));
    }

    // 2. Wrong order YYYY-DD-MM
    @Test
    public void testInvalidDateFormat_WrongOrder() throws Exception {
        mockMvc.perform(get("/api/v1/campaigns/active")
                        .param("date", "2025-40-12")) // invalid day 40
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid date format"))
                .andExpect(jsonPath("$.message").value("Expected YYYY-MM-DD"));
    }

    // 3. Completely invalid content
    @Test
    public void testInvalidDateFormat_NonNumeric() throws Exception {
        mockMvc.perform(get("/api/v1/campaigns/active")
                        .param("date", "hello-world"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid date format"))
                .andExpect(jsonPath("$.message").value("Expected YYYY-MM-DD"));
    }

    // 4. Edge case — correct pattern but invalid calendar date
    @Test
    public void testInvalidDateFormat_InvalidCalendarDate() throws Exception {
        mockMvc.perform(get("/api/v1/campaigns/active")
                        .param("date", "2025-02-30")) // Feb 30 does not exist
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid date format"))
                .andExpect(jsonPath("$.message").value("Expected YYYY-MM-DD"));
    }

    @Test
    public void testValidDateFormat_Returns200() throws Exception {

        when(campaignService.getActiveCampaignPerformance(LocalDate.of(2025, 11, 13)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/campaigns/active")
                        .param("date", "2025-11-13"))
                .andExpect(status().isOk());
    }
}
