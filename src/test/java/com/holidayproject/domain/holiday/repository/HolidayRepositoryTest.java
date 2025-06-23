package com.holidayproject.domain.holiday.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.holidayproject.IntegrationTestSupport;
import com.holidayproject.domain.holiday.dto.request.HolidaySearchRequest;
import com.holidayproject.domain.holiday.dto.response.HolidaySearchResponse;
import com.holidayproject.domain.holiday.entity.Holiday;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class HolidayRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private HolidayRepository holidayRepository;

    @Test
    @DisplayName("연도와 국가 코드로 공휴일 목록을 조회할 수 있다")
    void findAllByYearAndCountryCodeTest1() {
        // given
        Holiday h1 = createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1), "KR", 2024, true, true, null, null,
                null);
        Holiday h2 = createHoliday("설날", "Lunar New Year", LocalDate.of(2024, 2, 10), "KR", 2024, false, false, null,
                null, null);
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


    @Test
    @DisplayName("연도 필터로 조회할 수 있다")
    void searchByYear() {
        // given
        Holiday h1 = createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1), "KR", 2024, true, true, null, null,
                null);
        Holiday h2 = createHoliday("설날", "Lunar New Year", LocalDate.of(2023, 2, 10), "KR", 2023, false, false, null,
                null, null);
        holidayRepository.saveAll(List.of(h1, h2));

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .year(2024)
                .build();

        // when
        Page<HolidaySearchResponse> result = holidayRepository.searchHolidays(request, Pageable.ofSize(10));

        // then
        assertThat(result).hasSize(1)
                .extracting("date", "localName", "name", "countryCode", "year", "fixed", "global")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2024, 1, 1), "신정", "New Year's Day", "KR", 2024, true, true)
                );
    }

    @Test
    @DisplayName("날짜 범위로 조회할 수 있다")
    void searchByDateRange() {
        // given
        Holiday h1 = createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1), "KR", 2024, true, true, null, null,
                null);
        Holiday h2 = createHoliday("설날", "Lunar New Year", LocalDate.of(2024, 2, 10), "KR", 2024, false, false, null,
                null, null);
        holidayRepository.saveAll(List.of(h1, h2));

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .fromDate(LocalDate.of(2024, 1, 2))
                .toDate(LocalDate.of(2024, 12, 31))
                .build();

        // when
        Page<HolidaySearchResponse> result = holidayRepository.searchHolidays(request, Pageable.ofSize(10));

        // then
        assertThat(result).hasSize(1)
                .extracting("date", "localName", "name", "countryCode", "year", "fixed", "global")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2024, 2, 10), "설날", "Lunar New Year", "KR", 2024, false, false)
                );
    }

    @Test
    @DisplayName("이름(name)으로 조회할 수 있다")
    void searchByName() {
        // given
        Holiday h1 = createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1), "KR", 2024, true, true, null, null,
                null);
        Holiday h2 = createHoliday("설날", "Lunar New Year", LocalDate.of(2024, 2, 10), "KR", 2024, false, false, null,
                null, null);
        holidayRepository.saveAll(List.of(h1, h2));

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .name("Lunar")
                .build();

        // when
        Page<HolidaySearchResponse> result = holidayRepository.searchHolidays(request, Pageable.ofSize(10));

        // then
        assertThat(result).hasSize(1)
                .extracting("date", "localName", "name", "countryCode", "year", "fixed", "global")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2024, 2, 10), "설날", "Lunar New Year", "KR", 2024, false, false)
                );
    }

    @Test
    @DisplayName("로컬이름(localName)으로 조회할 수 있다")
    void searchByLocalName() {
        // given
        Holiday h1 = createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1), "KR", 2024, true, true, null, null,
                null);
        Holiday h2 = createHoliday("설날", "Lunar New Year", LocalDate.of(2024, 2, 10), "KR", 2024, false, false, null,
                null, null);
        holidayRepository.saveAll(List.of(h1, h2));

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .localName("설날")
                .build();

        // when
        Page<HolidaySearchResponse> result = holidayRepository.searchHolidays(request, Pageable.ofSize(10));

        // then
        assertThat(result).hasSize(1)
                .extracting("date", "localName", "name", "countryCode", "year", "fixed", "global")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2024, 2, 10), "설날", "Lunar New Year", "KR", 2024, false, false)
                );
    }

    @Test
    @DisplayName("국가코드로 조회할 수 있다")
    void searchByCountryCode() {
        // given
        Holiday h1 = createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1), "KR", 2024, true, true, null, null,
                null);
        Holiday h2 = createHoliday("설날", "Lunar New Year", LocalDate.of(2024, 2, 10), "KR", 2024, false, false, null,
                null, null);
        holidayRepository.saveAll(List.of(h1, h2));

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .countryCode("KR")
                .build();

        // when
        Page<HolidaySearchResponse> result = holidayRepository.searchHolidays(request, Pageable.ofSize(10));

        // then
        assertThat(result).hasSize(2)
                .extracting("date", "localName", "name", "countryCode", "year", "fixed", "global")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2024, 1, 1), "신정", "New Year's Day", "KR", 2024, true, true),
                        tuple(LocalDate.of(2024, 2, 10), "설날", "Lunar New Year", "KR", 2024, false, false)
                );
    }

    @Test
    @DisplayName("fixed 여부로 조회할 수 있다")
    void searchByFixed() {
        Holiday h1 = createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1), "KR", 2024, true, true, null, null,
                null);
        Holiday h2 = createHoliday("설날", "Lunar New Year", LocalDate.of(2024, 2, 10), "KR", 2024, false, false, null,
                null, null);
        holidayRepository.saveAll(List.of(h1, h2));

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .fixed(false)
                .build();

        Page<HolidaySearchResponse> result = holidayRepository.searchHolidays(request, Pageable.ofSize(10));

        assertThat(result).hasSize(1)
                .extracting("date", "localName", "name", "countryCode", "year", "fixed", "global")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2024, 2, 10), "설날", "Lunar New Year", "KR", 2024, false, false)
                );
    }

    @Test
    @DisplayName("global 여부로 조회할 수 있다")
    void searchByGlobal() {
        Holiday h1 = createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1), "KR", 2024, true, true, null, null,
                null);
        Holiday h2 = createHoliday("현충일", "Memorial Day", LocalDate.of(2024, 6, 6), "KR", 2024, true, false, null, null,
                null);
        holidayRepository.saveAll(List.of(h1, h2));

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .global(false)
                .build();

        Page<HolidaySearchResponse> result = holidayRepository.searchHolidays(request, Pageable.ofSize(10));

        assertThat(result).hasSize(1)
                .extracting("localName", "global")
                .containsExactlyInAnyOrder(tuple("현충일", false));
    }

    @Test
    @DisplayName("launchYear로 조회할 수 있다")
    void searchByLaunchYear() {
        Holiday h1 = createHoliday("광복절", "Independence Day", LocalDate.of(2024, 8, 15), "KR", 2024, true, true, 1945,
                null, null);
        Holiday h2 = createHoliday("제헌절", "Constitution Day", LocalDate.of(2024, 7, 17), "KR", 2024, true, true, 1948,
                null, null);
        holidayRepository.saveAll(List.of(h1, h2));

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .launchYear(1948)
                .build();

        Page<HolidaySearchResponse> result = holidayRepository.searchHolidays(request, Pageable.ofSize(10));

        assertThat(result).hasSize(1)
                .extracting("date", "localName", "name", "countryCode", "year", "fixed", "global", "launchYear")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2024, 7, 17), "제헌절", "Constitution Day", "KR", 2024, true, true, 1948));
    }

    @Test
    @DisplayName("types로 조회할 수 있다")
    void searchByTypes() {
        Holiday h1 = createHoliday("성탄절", "Christmas", LocalDate.of(2024, 12, 25), "KR", 2024, true, true, null, null,
                List.of("Public"));
        Holiday h2 = createHoliday("어린이날", "Children's Day", LocalDate.of(2024, 5, 5), "KR", 2024, true, true, null,
                null, null);
        holidayRepository.saveAll(List.of(h1, h2));

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .types(List.of("Public"))
                .build();

        Page<HolidaySearchResponse> result = holidayRepository.searchHolidays(request, Pageable.ofSize(10));

        assertThat(result).hasSize(1)
                .extracting("date", "localName", "name", "countryCode", "year", "fixed", "global")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2024, 12, 25), "성탄절", "Christmas", "KR", 2024, true, true)
                );
    }

    @Test
    @DisplayName("counties로 조회할 수 있다")
    void searchByCounties() {
        Holiday h1 = createHoliday("서울 기념일", "Local Day", LocalDate.of(2024, 3, 1), "KR", 2024, false, false, null,
                List.of("Seoul"), null);
        Holiday h2 = createHoliday("부산 기념일", "Local Day", LocalDate.of(2024, 3, 2), "KR", 2024, false, false, null,
                List.of("Busan"), null);
        holidayRepository.saveAll(List.of(h1, h2));

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .counties(List.of("Seoul"))
                .build();

        Page<HolidaySearchResponse> result = holidayRepository.searchHolidays(request, Pageable.ofSize(10));

        assertThat(result).hasSize(1)
                .extracting("date", "localName", "name", "countryCode", "year", "fixed", "global")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2024, 3, 1), "서울 기념일", "Local Day", "KR", 2024, false, false)
                );
    }

    @Test
    @DisplayName("복합 조건(년도,국가코드) 으로 조회할 수 있다")
    void searchByMultipleConditions() {
        Holiday h1 = createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1), "KR", 2024, true, true, null, null,
                null);
        Holiday h2 = createHoliday("설날", "Lunar New Year", LocalDate.of(2024, 2, 10), "KR", 2024, false, false, null,
                null, null);
        Holiday h3 = createHoliday("성탄절", "Christmas", LocalDate.of(2025, 12, 25), "KR", 2025, true, true, null, null,
                null);

        holidayRepository.saveAll(List.of(h1, h2, h3));

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .year(2024)
                .countryCode("KR")
                .build();

        Page<HolidaySearchResponse> result = holidayRepository.searchHolidays(request, Pageable.ofSize(10));

        assertThat(result).hasSize(2)
                .extracting("date", "localName", "name", "countryCode", "year", "fixed", "global")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2024, 1, 1), "신정", "New Year's Day", "KR", 2024, true, true),
                        tuple(LocalDate.of(2024, 2, 10), "설날", "Lunar New Year", "KR", 2024, false, false)
                );
    }

    @Test
    @DisplayName("복합 조건(기간,국가코드, 공휴일 타입등) 으로 조회할 수 있다")
    void searchByMultipleConditions2() {
        Holiday h1 = createHoliday("신정", "New Year's Day", LocalDate.of(2024, 1, 1), "KR", 2024, true, true, null, null,
                List.of("Public"));
        Holiday h2 = createHoliday("설날", "Lunar New Year", LocalDate.of(2024, 2, 10), "KR", 2024, false, false, null,
                null, List.of("Public"));
        Holiday h3 = createHoliday("성탄절", "Christmas", LocalDate.of(2025, 12, 25), "KR", 2025, true, true, null, null,
                null);

        holidayRepository.saveAll(List.of(h1, h2, h3));

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 12, 31))
                .countryCode("KR")
                .types(List.of("Public"))
                .build();

        Page<HolidaySearchResponse> result = holidayRepository.searchHolidays(request, Pageable.ofSize(10));

        assertThat(result).hasSize(2)
                .extracting("date", "localName", "name", "countryCode", "year", "fixed", "global")
                .containsExactlyInAnyOrder(
                        tuple(LocalDate.of(2024, 1, 1), "신정", "New Year's Day", "KR", 2024, true, true),
                        tuple(LocalDate.of(2024, 2, 10), "설날", "Lunar New Year", "KR", 2024, false, false)
                );
    }

    // 공통 Holiday 생성 메서드
    private Holiday createHoliday(String localName, String name, LocalDate date, String countryCode,
                                  int year, boolean fixed, boolean global,
                                  Integer launchYear, List<String> counties, List<String> types) {
        return Holiday.builder()
                .date(date)
                .localName(localName)
                .name(name)
                .countryCode(countryCode)
                .year(year)
                .fixed(fixed)
                .global(global)
                .launchYear(launchYear)
                .counties(counties)
                .types(types)
                .build();
    }
}