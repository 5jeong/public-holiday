package com.holidayproject.domain.holiday.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;

import com.holidayproject.IntegrationTestSupport;
import com.holidayproject.domain.api.HolidayApiClient;
import com.holidayproject.domain.holiday.dto.HolidayDto;
import com.holidayproject.domain.holiday.entity.Holiday;
import com.holidayproject.domain.holiday.repository.HolidayRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class HolidayUpsertServiceTest extends IntegrationTestSupport {

    @Autowired
    private HolidayUpsertService holidayUpsertService;

    @Autowired
    private HolidayRepository holidayRepository;

    @MockitoBean
    private HolidayApiClient holidayApiClient;

    @Test
    @DisplayName("저장된 데이터가 없을 때 외부 API 결과가 잘 저장된다")
    void upsertTest1() {
        int year = 2024;
        String countryCode = "KR";

        List<Holiday> apiHolidays = List.of(
                createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1)),
                createHoliday("설날", "Lunar New Year", LocalDate.of(2024, 2, 10))
        );

        given(holidayApiClient.getHolidays(year, countryCode)).willReturn(
                apiHolidays.stream().map(HolidayDto::of).toList()
        );

        // when
        holidayUpsertService.upsert(year, countryCode);

        // then
        List<Holiday> result = holidayRepository.findAllByYearAndCountryCode(year, countryCode);
        assertThat(result).hasSize(2)
                .extracting("localName", "name", "date")
                .containsExactlyInAnyOrder(
                        tuple("신정", "New Year's Day", LocalDate.of(2024, 1, 1)),
                        tuple("설날", "Lunar New Year", LocalDate.of(2024, 2, 10))
                );
    }

    @Test
    @DisplayName("기존 데이터와 외부 API 데이터가 달라도 외부 API로 덮어쓴다.")
    void upsertTest2() {
        int year = 2024;
        String countryCode = "KR";

        // 기존 데이터
        holidayRepository.save(createHoliday("기존", "Old", LocalDate.of(2024, 1, 1)));

        List<Holiday> apiHolidays = List.of(
                createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1)),
                createHoliday("설날", "Lunar New Year", LocalDate.of(2024, 2, 10))
        );

        given(holidayApiClient.getHolidays(year, countryCode)).willReturn(
                apiHolidays.stream().map(HolidayDto::of).toList()
        );

        // when
        holidayUpsertService.upsert(year, countryCode);

        // then
        List<Holiday> result = holidayRepository.findAllByYearAndCountryCode(year, countryCode);
        assertThat(result).hasSize(2)
                .extracting("localName", "name", "date")
                .containsExactlyInAnyOrder(
                        tuple("신정", "New Year's Day", LocalDate.of(2024, 1, 1)),
                        tuple("설날", "Lunar New Year", LocalDate.of(2024, 2, 10))
                );
    }


    @Test
    @DisplayName("기존 데이터와 외부 API 데이터가 같아도 외부 API로 덮어쓴다.")
    void upsertTest3() {
        int year = 2024;
        String countryCode = "KR";

        // 기존 데이터
        holidayRepository.save(createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1)));
        holidayRepository.save(createHoliday("설날", "Lunar New Year", LocalDate.of(2024, 2, 10)));

        List<Holiday> apiHolidays = List.of(
                createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1)),
                createHoliday("설날", "Lunar New Year", LocalDate.of(2024, 2, 10))
        );

        given(holidayApiClient.getHolidays(year, countryCode)).willReturn(
                apiHolidays.stream().map(HolidayDto::of).toList()
        );

        // when
        holidayUpsertService.upsert(year, countryCode);

        // then
        List<Holiday> result = holidayRepository.findAllByYearAndCountryCode(year, countryCode);
        assertThat(result).hasSize(2)
                .extracting("localName", "name", "date")
                .containsExactlyInAnyOrder(
                        tuple("신정", "New Year's Day", LocalDate.of(2024, 1, 1)),
                        tuple("설날", "Lunar New Year", LocalDate.of(2024, 2, 10))
                );
    }

    private Holiday createHoliday(String localName, String name, LocalDate date) {
        return Holiday.builder()
                .year(date.getYear())
                .countryCode("KR")
                .date(date)
                .localName(localName)
                .name(name)
                .fixed(true)
                .global(true)
                .build();
    }
}