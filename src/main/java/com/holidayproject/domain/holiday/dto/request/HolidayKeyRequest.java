package com.holidayproject.domain.holiday.dto.request;

public record HolidayKeyRequest(
        int year,
        String countryCode
) {
}
