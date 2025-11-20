package com.growthfusion.model;

import java.time.LocalDateTime;

public interface CostRow {
    String getCampaignName();
    String getStatus();
    Double getSpend();
    Long getImpressions();
    LocalDateTime getEventTimeLa();
}
