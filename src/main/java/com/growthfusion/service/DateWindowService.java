package com.growthfusion.service;

import com.growthfusion.dto.DateWindowDto;

import java.time.LocalDate;

/**
 * Service interface responsible for converting an input LA date
 * (YYYY-MM-DD) into its corresponding UTC window.
 */
public interface DateWindowService {

    DateWindowDto computeUtcWindow(LocalDate date);
}
