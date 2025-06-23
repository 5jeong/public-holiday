package com.holidayproject.domain.holiday.service.facade;

import com.holidayproject.domain.holiday.dto.request.HolidaySearchRequest;
import com.holidayproject.domain.holiday.dto.response.HolidaySearchResponse;
import com.holidayproject.domain.holiday.dto.response.SuccessMessageResponse;
import com.holidayproject.domain.holiday.service.HolidayBatchAsyncService;
import com.holidayproject.domain.holiday.service.HolidayDeleteService;
import com.holidayproject.domain.holiday.service.HolidaySearchService;
import com.holidayproject.domain.holiday.service.HolidayUpsertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HolidayFacadeService {

    private final HolidayBatchAsyncService holidayBatchAsyncService;
    private final HolidaySearchService holidaySearchService;
    private final HolidayUpsertService holidayUpsertService;
    private final HolidayDeleteService holidayDeleteService;

    // 최근 5년치 비동기 저장 (비동기 처리)
    public SuccessMessageResponse saveRecentFiveYearsAsync() {
        return holidayBatchAsyncService.saveHolidaysForFiveRecentYears();
    }

    // 조건 기반 검색
    public Page<HolidaySearchResponse> search(HolidaySearchRequest request, Pageable pageable) {
        return holidaySearchService.searchHolidays(request, pageable);
    }

    // 연도+국가 기반 upsert
    @Transactional
    public SuccessMessageResponse upsert(int year, String countryCode) {
        return holidayUpsertService.upsert(year, countryCode);
    }

    // 연도+국가 기반 삭제
    @Transactional
    public SuccessMessageResponse deleteByYearAndCountry(int year, String countryCode) {
        return holidayDeleteService.deleteByYearAndCountry(year, countryCode);
    }

}
