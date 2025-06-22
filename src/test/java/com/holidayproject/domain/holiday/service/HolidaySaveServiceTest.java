package com.holidayproject.domain.holiday.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.given;

import com.holidayproject.IntegrationTestSupport;
import com.holidayproject.domain.api.HolidayApiClient;
import com.holidayproject.domain.holiday.dto.HolidayDto;
import com.holidayproject.domain.holiday.entity.Holiday;
import com.holidayproject.domain.holiday.repository.HolidayRepository;
import com.holidayproject.global.exception.BusinessException;
import com.holidayproject.global.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class HolidaySaveServiceTest extends IntegrationTestSupport {
    @Autowired
    private HolidaySaveService holidaySaveService;

    @Autowired
    private HolidayRepository holidayRepository;

    @MockitoBean
    private HolidayApiClient holidayApiClient;

    @DisplayName("신규 공휴일이 존재할 경우 저장된다")
    @Test
    void saveHolidayTest1() {
        // given
        String countryCode = "KR";
        int year = 2025;

        List<HolidayDto> apiResponse = List.of(
                new HolidayDto(LocalDate.of(2025, 1, 1), "새해", "New Year's Day", "KR", false, true, null, null,
                        List.of("Public"))
        );

        given(holidayApiClient.getHolidays(year, countryCode)).willReturn(apiResponse);

        // when
        holidaySaveService.saveHoliday(year, countryCode);

        // then
        List<Holiday> saved = holidayRepository.findAll();
        assertThat(saved).hasSize(1)
                .extracting("date", "localName", "name", "countryCode")
                .containsExactly(tuple(LocalDate.of(2025, 1, 1), "새해", "New Year's Day", "KR"));
    }

    @DisplayName("기존과 신규 공휴일이 섞여있을 경우, 신규공휴일만 저장된다")
    @Test
    void saveHolidayTest2() {
        // given
        String countryCode = "KR";
        int year = 2025;

        holidayRepository.save(Holiday.builder()
                .year(year)
                .date(LocalDate.of(2025, 1, 1))
                .localName("새해")
                .name("New Year's Day")
                .countryCode(countryCode)
                .fixed(false)
                .global(true)
                .types(List.of("Public"))
                .build()
        );

        List<HolidayDto> apiResponse = List.of(
                new HolidayDto(LocalDate.of(2025, 1, 1), "새해", "New Year's Day", "KR", false, true, null, null,
                        List.of("Public")),
                new HolidayDto(LocalDate.of(2025, 11, 11), "새로운 공휴일", "new holiday", "KR", false, true, null,
                        null, List.of("Public"))
        );

        given(holidayApiClient.getHolidays(year, countryCode)).willReturn(apiResponse);

        // when
        holidaySaveService.saveHoliday(year, countryCode);

        // then
        List<Holiday> saved = holidayRepository.findAll();
        assertThat(saved).hasSize(2)
                .extracting("date", "localName")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2025, 1, 1), "새해"),
                        tuple(LocalDate.of(2025, 11, 11), "새로운 공휴일")
                );
    }

    @DisplayName("신규 공휴일이 존재하지 않을 경우 저장되지 않는다")
    @Test
    void saveHolidayTest3() {
        // given
        String countryCode = "KR";
        int year = 2025;

        // 기존 DB에 이미 하나 저장
        Holiday existingHoliday = Holiday.builder()
                .year(year)
                .date(LocalDate.of(2025, 1, 1))
                .localName("새해")
                .name("New Year's Day")
                .countryCode(countryCode)
                .fixed(false)
                .global(true)
                .launchYear(null)
                .counties(null)
                .types(List.of("Public"))
                .build();

        holidayRepository.save(existingHoliday);

        // API 응답도 동일한 날짜의 데이터
        List<HolidayDto> apiResponse = List.of(HolidayDto.of(existingHoliday));

        given(holidayApiClient.getHolidays(year, countryCode)).willReturn(apiResponse);

        // when
        holidaySaveService.saveHoliday(year, countryCode);

        // then
        List<Holiday> saved = holidayRepository.findAll();
        assertThat(saved).hasSize(1) // 새로운 저장 없이 기존 1건만 존재
                .extracting("date", "localName", "name", "countryCode")
                .containsExactly(tuple(LocalDate.of(2025, 1, 1), "새해", "New Year's Day", "KR"));
    }

    @DisplayName("API 호출 중 예외가 발생하면 저장되지 않는다")
    @Test
    void saveHolidayTest4() {
        // given
        String countryCode = "KR";
        int year = 2025;

        given(holidayApiClient.getHolidays(year, countryCode)).willThrow(
                new BusinessException(ErrorCode.NAGER_DATE_API_ERROR));

        // when & then
        assertThatThrownBy(() -> holidaySaveService.saveHoliday(year, countryCode))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NAGER_DATE_API_ERROR.getMessage());

        assertThat(holidayRepository.findAll()).isEmpty();
    }
}