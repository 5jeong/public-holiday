package com.holidayproject.domain.holiday.repository;

import com.holidayproject.domain.holiday.dto.request.HolidaySearchRequest;
import com.holidayproject.domain.holiday.dto.response.HolidayResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayRepositoryCustom {

    Page<HolidayResponse> searchHolidays(HolidaySearchRequest request, Pageable pageable);
}
