package com.holidayproject.domain.holiday.service;

import com.holidayproject.domain.country.repository.CountryRepository;
import com.holidayproject.domain.holiday.repository.HolidayRepository;
import com.holidayproject.global.exception.BusinessException;
import com.holidayproject.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayDeleteService {

    private final HolidayRepository holidayRepository;
    private final CountryRepository countryRepository;

    @Transactional
    public void deleteByYearAndCountry(int year, String countryCode) {
        if (!countryRepository.existsByCountryCode(countryCode)) {
            throw new BusinessException(ErrorCode.INVALID_COUNTRY_CODE);
        }
        holidayRepository.deleteByYearAndCountryCode(year, countryCode);
        log.info("연도: {}, 국가: {} 공휴일 전체 삭제 완료", year, countryCode);
    }
}
