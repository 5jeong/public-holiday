package com.holidayproject.domain.holiday.dto.response;

import com.holidayproject.domain.holiday.entity.Holiday;
import java.time.LocalDate;
import java.util.List;

public record HolidaySearchResponse(
        Integer year,
        LocalDate date,
        String localName,
        String name,
        String countryCode,
        boolean fixed,
        boolean global,
        Integer launchYear,
        List<String> counties,
        List<String> types
) {
    public static HolidaySearchResponse of(Holiday h) {
        return new HolidaySearchResponse(
                h.getYear(),
                h.getDate(),
                h.getLocalName(),
                h.getName(),
                h.getCountryCode(),
                h.isFixed(),
                h.isGlobal(),
                h.getLaunchYear(),
                h.getCounties(),
                h.getTypes()
        );
    }
}