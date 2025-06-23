package com.holidayproject.domain.holiday.repository;

import com.holidayproject.domain.holiday.dto.request.HolidaySearchRequest;
import com.holidayproject.domain.holiday.dto.response.HolidaySearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayRepositoryCustom {

    Page<HolidaySearchResponse> searchHolidays(HolidaySearchRequest request, Pageable pageable);
}
