package com.holidayproject.domain.holiday.dto;

import com.holidayproject.domain.holiday.entity.Holiday;
import java.time.LocalDate;
import java.util.List;

public record HolidayDto(
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
    public static HolidayDto of(Holiday holiday) {
        return new HolidayDto(
                holiday.getDate(),
                holiday.getLocalName(),
                holiday.getName(),
                holiday.getCountryCode(),
                holiday.isFixed(),
                holiday.isGlobal(),
                holiday.getLaunchYear(),
                holiday.getCounties(),
                holiday.getTypes()
        );
    }
}