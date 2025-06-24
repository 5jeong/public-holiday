package com.holidayproject.domain.holiday.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record HolidayKeyRequest(

        @Schema(description = "조회할 연도", example = "2024")
        int year,

        @Schema(description = "국가 코드", example = "KR")
        String countryCode
) {
}
