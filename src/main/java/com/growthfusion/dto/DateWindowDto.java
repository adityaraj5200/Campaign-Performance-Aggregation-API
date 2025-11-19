package com.growthfusion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents the computed LA→UTC date window.
 * Used internally by the service layer for querying cost & revenue data.
 */
@Data
@AllArgsConstructor
public class DateWindowDto {
    private LocalDateTime utcStart;
    private LocalDateTime utcEnd;
}
