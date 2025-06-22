package com.holidayproject.domain.holiday.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.holidayproject.IntegrationTestSupport;
import com.holidayproject.domain.holiday.entity.Holiday;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class HolidayRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private HolidayRepository holidayRepository;

    @Test
    @DisplayName("연도와 국가 코드로 공휴일 목록을 조회할 수 있다")
    void findAllByYearAndCountryCodeTest1() {
        // given
        Holiday h1 = Holiday.builder()
                .date(LocalDate.of(2024, 1, 1))
                .localName("신정")
                .name("New Year's Day")
                .countryCode("KR")
                .year(2024)
                .fixed(true)
                .global(true)
                .build();

        Holiday h2 = Holiday.builder()
                .date(LocalDate.of(2024, 2, 10))
                .localName("설날")
                .name("Lunar New Year")
                .countryCode("KR")
                .year(2024)
                .fixed(false)
                .global(false)
                .build();

        holidayRepository.saveAll(List.of(h1, h2));

        // when
        List<Holiday> result = holidayRepository.findAllByYearAndCountryCode(2024, "KR");

        // then
        assertThat(result).hasSize(2)
                .extracting("date", "localName", "name", "countryCode", "year", "fixed", "global")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2024, 1, 1), "신정", "New Year's Day", "KR", 2024, true, true),
                        tuple(LocalDate.of(2024, 2, 10), "설날", "Lunar New Year", "KR", 2024, false, false)
                );
    }
}