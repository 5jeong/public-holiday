package com.holidayproject.domain.holiday.service;

import com.holidayproject.domain.holiday.dto.request.HolidaySearchRequest;
import com.holidayproject.domain.holiday.dto.response.HolidaySearchResponse;
import com.holidayproject.domain.holiday.repository.HolidayRepository;
import com.holidayproject.global.common.PageResponse;
import com.holidayproject.global.exception.BusinessException;
import com.holidayproject.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HolidaySearchService {

    private final HolidayRepository holidayRepository;

    public PageResponse<HolidaySearchResponse> searchHolidays(HolidaySearchRequest request, Pageable pageable) {
        validateSearchRequest(request, pageable);
        return new PageResponse<>(holidayRepository.searchHolidays(request, pageable));
    }

    private void validateSearchRequest(HolidaySearchRequest request, Pageable pageable) {
        if (request.year() != null && (request.fromDate() != null || request.toDate() != null)) {
            throw new BusinessException(ErrorCode.INVALID_SEARCH_CONDITION);
        }
        if (request.fromDate() != null && request.toDate() != null && request.fromDate().isAfter(request.toDate())) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
        }
    }
}