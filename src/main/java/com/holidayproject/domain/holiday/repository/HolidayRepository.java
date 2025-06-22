package com.holidayproject.domain.holiday.repository;

import com.holidayproject.domain.holiday.entity.Holiday;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    List<Holiday> findAllByYearAndCountryCode(int year, String countryCode);
}
