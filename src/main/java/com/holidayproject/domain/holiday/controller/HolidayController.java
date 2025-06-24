package com.holidayproject.domain.holiday.controller;

import com.holidayproject.domain.holiday.dto.request.HolidayKeyRequest;
import com.holidayproject.domain.holiday.dto.request.HolidaySearchRequest;
import com.holidayproject.domain.holiday.dto.response.HolidaySearchResponse;
import com.holidayproject.domain.holiday.dto.response.SuccessMessageResponse;
import com.holidayproject.domain.holiday.service.facade.HolidayFacadeService;
import com.holidayproject.global.common.ApiResponse;
import com.holidayproject.global.common.PageResponse;
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
public class HolidayController {

    private final HolidayFacadeService holidayFacadeService;

    @PostMapping("/save-recent")
    public ApiResponse<SuccessMessageResponse> loadRecentHolidays() {
        return ApiResponse.success(holidayFacadeService.saveRecentFiveYearsAsync());
    }

    @PostMapping("/search")
    public ApiResponse<PageResponse<HolidaySearchResponse>> searchHolidays(
            @RequestBody HolidaySearchRequest request,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("date").ascending());
        return ApiResponse.success(holidayFacadeService.search(request, pageable));
    }

    @PutMapping("/upsert")
    public ApiResponse<SuccessMessageResponse> upsertHolidays(@RequestBody HolidayKeyRequest request) {
        return ApiResponse.success(holidayFacadeService.upsert(request.year(), request.countryCode()));
    }

    @DeleteMapping("/{year}/{countryCode}")
    public ApiResponse<SuccessMessageResponse> deleteHolidays(@PathVariable int year,
                                                              @PathVariable String countryCode) {
        return ApiResponse.success(holidayFacadeService.deleteByYearAndCountry(year, countryCode));
    }

}
