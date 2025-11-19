package com.growthfusion.service.impl;

import com.growthfusion.dto.DateWindowDto;
import com.growthfusion.service.DateWindowService;
import com.growthfusion.util.DateWindowUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Implementation of DateWindowService.
 * Delegates LA→UTC conversion logic to DateWindowUtil.
 */
@Service
public class DateWindowServiceImpl implements DateWindowService {

    @Override
    public DateWindowDto computeUtcWindow(LocalDate date) {
        return DateWindowUtil.toUtcWindow(date);
    }
}
