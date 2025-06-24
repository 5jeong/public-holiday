package com.holidayproject.domain.holiday.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.holidayproject.domain.holiday.dto.request.HolidaySearchRequest;
import com.holidayproject.domain.holiday.dto.response.HolidaySearchResponse;
import com.holidayproject.domain.holiday.repository.HolidayRepository;
import com.holidayproject.global.common.PageResponse;
import com.holidayproject.global.exception.BusinessException;
import com.holidayproject.global.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class HolidaySearchServiceTest {

    @InjectMocks
    private HolidaySearchService holidaySearchService;

    @Mock
    private HolidayRepository holidayRepository;

    @Test
    @DisplayName("정상적인 조건으로 공휴일을 검색할 수 있다")
    void searchHolidaysTest1() {
        // given
        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .year(2024)
                .countryCode("KR")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<HolidaySearchResponse> mockPage = new PageImpl<>(List.of());
        given(holidayRepository.searchHolidays(any(), any())).willReturn(mockPage);

        // when
        PageResponse<HolidaySearchResponse> result = holidaySearchService.searchHolidays(
                request, pageable);

        // then
        assertThat(result).isNotNull();
        verify(holidayRepository, times(1)).searchHolidays(request, pageable);
    }

    @Test
    @DisplayName("연도와 날짜 범위를 동시에 입력하면 예외(INVALID_SEARCH_CONDITION)가 발생한다")
    void searchHolidaysTest2() {
        // given
        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .year(2024)
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 12, 31))
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        // when & then
        assertThatThrownBy(() -> holidaySearchService.searchHolidays(request, pageable))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_SEARCH_CONDITION.getMessage());

        verify(holidayRepository, never()).searchHolidays(any(), any()); // 레파지토리 호출 x
    }

    @Test
    @DisplayName("fromDate가 toDate보다 크면 예외(INVALID_DATE_RANGE)가 발생한다")
    void searchHolidaysTest3() {
        // given
        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .fromDate(LocalDate.of(2024, 12, 31))
                .toDate(LocalDate.of(2024, 1, 1))
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        // when & then
        assertThatThrownBy(() -> holidaySearchService.searchHolidays(request, pageable))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_DATE_RANGE.getMessage());

        verify(holidayRepository, never()).searchHolidays(any(), any());  // 레파지토리 호출 x
    }

}