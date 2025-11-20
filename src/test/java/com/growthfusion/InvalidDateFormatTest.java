package com.growthfusion;

import com.growthfusion.controller.CampaignController;
import com.growthfusion.service.CampaignService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CampaignController.class)
public class InvalidDateFormatTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock CampaignService because we are only testing date handling + exception layer
    @MockBean
    private CampaignService campaignService;

    @Test
    public void testInvalidDateFormat() throws Exception {

        mockMvc.perform(get("/api/v1/campaigns/active")
                        .param("date", "13-11-2025"))  // WRONG FORMAT
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid date format"))
                .andExpect(jsonPath("$.message").value("Expected YYYY-MM-DD"));
    }
}
