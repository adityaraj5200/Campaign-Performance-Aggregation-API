package com.growthfusion.service.impl;

import com.growthfusion.dto.DateWindowDto;
import com.growthfusion.service.DateWindowService;
import com.growthfusion.util.DateWindowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Implementation of DateWindowService.
 * Delegates LA→UTC conversion logic to DateWindowUtil.
 */
@Slf4j
@Service
public class DateWindowServiceImpl implements DateWindowService {

    @Override
    public DateWindowDto computeUtcWindow(LocalDate date) {
        ZonedDateTime laStart = date.atStartOfDay(ZoneId.of("America/Los_Angeles"));
        ZonedDateTime laEnd   = laStart.plusDays(1).minusSeconds(1);

        ZonedDateTime utcStart = laStart.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime utcEnd   = laEnd.withZoneSameInstant(ZoneId.of("UTC"));

        log.debug("LA → UTC conversion: LA({} → {}), LA({} → {})", laStart, utcStart, laEnd, utcEnd);

        return new DateWindowDto(
                utcStart.toLocalDateTime(),
                utcEnd.toLocalDateTime()
        );
    }
}
