package com.holidayproject.domain.holiday.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.holidayproject.IntegrationTestSupport;
import com.holidayproject.domain.holiday.entity.Holiday;
import com.holidayproject.domain.holiday.repository.HolidayRepository;
import com.holidayproject.global.exception.BusinessException;
import com.holidayproject.global.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class HolidayDeleteServiceTest extends IntegrationTestSupport {


    @Autowired
    private HolidayDeleteService holidayDeleteService;

    @Autowired
    private HolidayRepository holidayRepository;

    @Test
    @DisplayName("존재하는 국가 코드,유효한 년도일 경우 공휴일 삭제에 성공한다")
    void delete_success() {
        //given
        holidayRepository.save(Holiday.builder()
                .date(LocalDate.of(2024, 1, 1))
                .localName("신정")
                .name("New Year's Day")
                .countryCode("KR")
                .year(2024)
                .fixed(true)
                .global(true)
                .build());

        // when
        holidayDeleteService.deleteByYearAndCountry(2024, "KR");

        // then
        List<Holiday> result = holidayRepository.findAllByYearAndCountryCode(2024, "KR");
        assertThat(result).isEmpty();
    }

    @DisplayName("존재하지 않는 국가 코드일 경우 예외가 발생한다")
    @Test
    void delete_fail() {
        //given
        holidayRepository.save(Holiday.builder()
                .date(LocalDate.of(2024, 1, 1))
                .localName("신정")
                .name("New Year's Day")
                .countryCode("KR")
                .year(2024)
                .fixed(true)
                .global(true)
                .build());

        // when & then
        assertThatThrownBy(() -> holidayDeleteService.deleteByYearAndCountry(2024, "NOT_FOUND"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_COUNTRY_CODE.getMessage());
    }

    @Test
    @DisplayName("연도가 1975 미만일 경우 예외가 발생한다")
    void delete_fail2() {
        assertThatThrownBy(() -> holidayDeleteService.deleteByYearAndCountry(1974, "KR"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_YEAR.getMessage());
    }

    @Test
    @DisplayName("연도가 2075 초과일 경우 예외가 발생한다")
    void delete_fail3() {
        assertThatThrownBy(() -> holidayDeleteService.deleteByYearAndCountry(2076, "KR"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_YEAR.getMessage());
    }

}