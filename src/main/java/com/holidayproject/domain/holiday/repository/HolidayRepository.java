package com.holidayproject.domain.holiday.repository;

import com.holidayproject.domain.holiday.entity.Holiday;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayRepositoryCustom {
    List<Holiday> findAllByYearAndCountryCode(int year, String countryCode);

    @Modifying
    @Query("DELETE FROM Holiday h WHERE h.year = :year AND h.countryCode = :countryCode")
    void deleteByYearAndCountryCode(int year, String countryCode);
}
