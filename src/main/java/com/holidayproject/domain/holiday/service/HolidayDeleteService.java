package com.holidayproject.domain.holiday.service;

import com.holidayproject.domain.country.repository.CountryRepository;
import com.holidayproject.domain.holiday.dto.response.SuccessMessageResponse;
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

    private static final int MIN_YEAR = 1975;
    private static final int MAX_YEAR = 2075;
    public static final String DELETE_SUCCESS_MESSAGE = "%d, %s 데이터 레코드 삭제 완료";
    private final HolidayRepository holidayRepository;
    private final CountryRepository countryRepository;

    @Transactional
    public SuccessMessageResponse deleteByYearAndCountry(int year, String countryCode) {

        validateRequest(year, countryCode);
        holidayRepository.deleteByYearAndCountryCode(year, countryCode);

        log.info("연도: {}, 국가: {} 공휴일 전체 삭제 완료", year, countryCode);
        return SuccessMessageResponse.of(String.format(DELETE_SUCCESS_MESSAGE, year, countryCode));
    }

    private void validateRequest(int year, String countryCode) {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new BusinessException(ErrorCode.INVALID_YEAR);
        }
        if (!countryRepository.existsByCountryCode(countryCode)) {
            throw new BusinessException(ErrorCode.INVALID_COUNTRY_CODE);
        }
    }
}
