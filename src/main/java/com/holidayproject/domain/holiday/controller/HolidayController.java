package com.holidayproject.domain.holiday.controller;

import com.holidayproject.domain.holiday.dto.request.HolidayKeyRequest;
import com.holidayproject.domain.holiday.dto.request.HolidaySearchRequest;
import com.holidayproject.domain.holiday.dto.response.HolidaySearchResponse;
import com.holidayproject.domain.holiday.dto.response.SuccessMessageResponse;
import com.holidayproject.domain.holiday.service.facade.HolidayFacadeService;
import com.holidayproject.global.common.ApiResponse;
import com.holidayproject.global.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/holidays")
@Tag(name = "Holiday API", description = "전 세계 공휴일 데이터를 수집·검색·갱신·삭제하는 API")
public class HolidayController {

    private final HolidayFacadeService holidayFacadeService;

    @Operation(
            summary = "최근 5년치 공휴일 데이터 적재",
            description = """
                        외부 공공 API를 호출하여 모든 국가의 최근 5년간 공휴일 데이터를 수집합니다.
                        이 작업은 내부적으로 비동기로 수행되며, 국가별로 연도마다 공휴일 데이터를 저장합니다.
                        실행 시간은 국가 수(109개 기준)와 연도 수에 비례하여 소요될 수 있습니다.
                    """
    )
    @PostMapping("/save-recent")
    public ApiResponse<SuccessMessageResponse> loadRecentHolidays() {
        return ApiResponse.success(holidayFacadeService.saveRecentFiveYearsAsync());
    }

    @Operation(
            summary = "공휴일 검색",
            description = """
                        검색 조건에 따라 공휴일 데이터를 페이징 형태로 조회합니다.
                        검색 조건은 요청 Body로 전달되며, 정렬은 기본적으로 날짜(date) 기준 오름차순입니다.
                    """
    )
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1"),
            @Parameter(name = "size", description = "페이지 크기", example = "10")
    })
    @PostMapping("/search")
    public ApiResponse<PageResponse<HolidaySearchResponse>> searchHolidays(
            @RequestBody HolidaySearchRequest request,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("date").ascending());
        return ApiResponse.success(holidayFacadeService.search(request, pageable));
    }

    @Operation(
            summary = "공휴일 재동기화 (Upsert)",
            description = """
                        특정 연도 및 국가에 대해 공공 API를 다시 호출하여 기존 공휴일 데이터를 덮어씁니다.
                        이미 존재하는 경우 업데이트, 없는 경우 새로 저장하도록 동작합니다..
                    """
    )
    @PutMapping("/upsert")
    public ApiResponse<SuccessMessageResponse> upsertHolidays(@RequestBody HolidayKeyRequest request) {
        return ApiResponse.success(holidayFacadeService.upsert(request.year(), request.countryCode()));
    }

    @Operation(
            summary = "공휴일 삭제",
            description = """
                        특정 연도 및 국가 코드에 해당하는 공휴일 데이터를 모두 삭제합니다.
                        데이터가 존재하지 않아도 오류 없이 처리됩니다.
                    """
    )
    @Parameters({
            @Parameter(name = "year", description = "삭제할 연도", example = "2024"),
            @Parameter(name = "countryCode", description = " 국가 코드", example = "KR")
    })
    @DeleteMapping("/{year}/{countryCode}")
    public ApiResponse<SuccessMessageResponse> deleteHolidays(
            @PathVariable int year,
            @PathVariable String countryCode
    ) {
        return ApiResponse.success(holidayFacadeService.deleteByYearAndCountry(year, countryCode));
    }
}
