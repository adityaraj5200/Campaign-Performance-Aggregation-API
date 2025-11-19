package com.growthfusion.util;

import com.growthfusion.dto.DateWindowDto;

import java.time.*;

/**
 * Utility for converting an input LA date (YYYY-MM-DD)
 * into the required UTC window:
 * LA: 00:00 → 23:59:59  ⟶  UTC: 08:00 → 07:59:59 next day
 */
public final class DateWindowUtil {

    private static final ZoneId LA_ZONE = ZoneId.of("America/Los_Angeles");
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    private DateWindowUtil() {}

    public static DateWindowDto toUtcWindow(LocalDate date) {

        // LA DAY START: 00:00:00
        LocalDateTime laStart = date.atStartOfDay();

        // LA DAY END: 23:59:59
        LocalDateTime laEnd = date.atTime(23, 59, 59);

        // Convert LA → UTC
        LocalDateTime utcStart = laStart.atZone(LA_ZONE).withZoneSameInstant(UTC_ZONE).toLocalDateTime();
        LocalDateTime utcEnd = laEnd.atZone(LA_ZONE).withZoneSameInstant(UTC_ZONE).toLocalDateTime();

        return new DateWindowDto(utcStart, utcEnd);
    }
}
