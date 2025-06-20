package com.holidayproject.domain.country.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.holidayproject.domain.api.CountryApiClient;
import com.holidayproject.domain.country.dto.CountryDto;
import com.holidayproject.domain.country.entity.Country;
import com.holidayproject.domain.country.repository.CountryRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {
    @Mock
    private CountryApiClient countryApiClient;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    @DisplayName("DB에 저장되지않은 국가가 존재할경우 새로 저장한다.")
    @Test
    void CountryServiceTest1() {
        // given
        CountryDto dto1 = new CountryDto("KR", "Korea");
        CountryDto dto2 = new CountryDto("US", "United States");

        given(countryApiClient.getAvailableCountries()).willReturn(List.of(dto1, dto2));
        given(countryRepository.findAll()).willReturn(List.of());

        // when
        countryService.initCountries();

        // then
        ArgumentCaptor<List<Country>> captor = ArgumentCaptor.forClass(List.class);
        verify(countryRepository, times(1)).saveAll(captor.capture());

        List<Country> saved = captor.getValue();
        assertThat(saved).hasSize(2)
                .extracting("countryCode", "name")
                .containsExactlyInAnyOrder(
                        tuple("KR", "Korea"),
                        tuple("US", "United States")
                );
    }

    @DisplayName("모든 국가가 이미 저장된 경우 저장 로직은 실행되지 않는다.")
    @Test
    void CountryServiceTest2() {
        // given
        CountryDto dto = new CountryDto("KR", "Korea");
        Country existing = Country.from(dto);

        given(countryApiClient.getAvailableCountries()).willReturn(List.of(dto));
        given(countryRepository.findAll()).willReturn(List.of(existing));

        // when
        countryService.initCountries();

        // then
        verify(countryRepository, never()).saveAll(any());
    }

    @DisplayName("일부 국가만 DB에 없는 경우, 없는 국가만 저장한다.")
    @Test
    void CountryServiceTest3() {
        // given
        CountryDto dto1 = new CountryDto("KR", "Korea");
        CountryDto dto2 = new CountryDto("US", "United States");

        Country existing = Country.from(dto1); // KR은 이미 있음

        given(countryApiClient.getAvailableCountries()).willReturn(List.of(dto1, dto2));
        given(countryRepository.findAll()).willReturn(List.of(existing));

        // when
        countryService.initCountries();

        // then
        ArgumentCaptor<List<Country>> captor = ArgumentCaptor.forClass(List.class);
        verify(countryRepository, times(1)).saveAll(captor.capture());

        List<Country> saved = captor.getValue();
        assertThat(saved).hasSize(1)
                .extracting("countryCode", "name")
                .containsExactly(tuple("US", "United States"));
    }
}