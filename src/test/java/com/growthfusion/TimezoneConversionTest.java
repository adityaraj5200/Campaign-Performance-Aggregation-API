package com.growthfusion;

//import com.growthfusion.service.impl.DateWindowServiceImpl;


import com.growthfusion.dto.DateWindowDto;
import com.growthfusion.service.DateWindowService;
import com.growthfusion.service.impl.DateWindowServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tests LA → UTC date window conversion for correctness.
 */
public class TimezoneConversionTest {

    private final DateWindowService service = new DateWindowServiceImpl();

    @Test
    public void testWindowForNov13_2025() {

        LocalDate date = LocalDate.of(2025, 11, 13);
        DateWindowDto window = service.computeUtcWindow(date);

        LocalDateTime expectedStart = LocalDateTime.of(2025, 11, 13, 8, 0, 0);
        LocalDateTime expectedEnd   = LocalDateTime.of(2025, 11, 14, 7, 59, 59);

        Assertions.assertEquals(expectedStart, window.getUtcStart());
        Assertions.assertEquals(expectedEnd, window.getUtcEnd());
    }

    @Test
    public void testWindowForMarch5_2025() {

        LocalDate date = LocalDate.of(2025, 3, 5);
        DateWindowDto window = service.computeUtcWindow(date);

        LocalDateTime expectedStart = LocalDateTime.of(2025, 3, 5, 8, 0, 0);
        LocalDateTime expectedEnd   = LocalDateTime.of(2025, 3, 6, 7, 59, 59);

        Assertions.assertEquals(expectedStart, window.getUtcStart());
        Assertions.assertEquals(expectedEnd, window.getUtcEnd());
    }

    @Test
    public void testWindowForDSTBoundary() {

        // Around US Daylight Saving Time change.
        LocalDate date = LocalDate.of(2025, 3, 9); // DST switch date
        DateWindowDto window = service.computeUtcWindow(date);

        // On this specific date in 2025, LA still converts to UTC+8 at midnight.
        LocalDateTime expectedStart = LocalDateTime.of(2025, 3, 9, 8, 0, 0);
        LocalDateTime expectedEnd   = LocalDateTime.of(2025, 3, 10, 6, 59, 59);

        Assertions.assertEquals(expectedStart, window.getUtcStart());
        Assertions.assertEquals(expectedEnd, window.getUtcEnd());
    }
}
