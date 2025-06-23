package com.holidayproject.domain.holiday.dto.request;


import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record HolidaySearchRequest(
        Integer year,
        LocalDate fromDate,
        LocalDate toDate,
        String localName,
        String name,
        String countryCode,
        Boolean fixed,
        Boolean global,
        Integer launchYear,
        List<String> counties,
        List<String> types
) {
}
