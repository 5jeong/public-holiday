package com.holidayproject.domain.holiday.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record HolidaySearchRequest(

        @Schema(description = "조회할 연도", example = "2024")
        Integer year,

        @Schema(description = "조회 시작일", example = "2024-01-01")
        LocalDate fromDate,

        @Schema(description = "조회 종료일", example = "2024-12-31")
        LocalDate toDate,

        @Schema(description = "공휴일 이름", example = "New Year's Day")
        String name,

        @Schema(description = "로컬 공휴일 이름", example = "신정")
        String localName,

        @Schema(description = "국가 코드", example = "KR")
        String countryCode,

        @Schema(description = "고정 날짜 여부 (true: 매년 같은 날짜)", example = "true")
        Boolean fixed,

        @Schema(description = "글로벌 공휴일 여부", example = "false")
        Boolean global,

        @Schema(description = "공휴일이 최초 도입된 연도", example = "1950")
        Integer launchYear,

        @Schema(description = "공휴일이 적용되는 지역 코드 리스트", example = "[\"US-NY\", \"US-CA\"]")
        List<String> counties,

        @Schema(description = "공휴일 유형 리스트 (예: PUBLIC)", example =  "[\"PUBLIC\"]")
        List<String> types
) {
}
