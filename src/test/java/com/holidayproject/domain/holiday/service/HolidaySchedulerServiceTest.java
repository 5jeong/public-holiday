package com.holidayproject.domain.holiday.service;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.holidayproject.IntegrationTestSupport;
import com.holidayproject.domain.country.entity.Country;
import com.holidayproject.domain.country.repository.CountryRepository;
import java.time.Year;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class HolidaySchedulerServiceTest extends IntegrationTestSupport {

    @Autowired
    private HolidaySchedulerService holidaySchedulerService;

    @Autowired
    private CountryRepository countryRepository;

    @MockitoBean
    private HolidayUpsertService holidayUpsertService;

    @Test
    @DisplayName("모든 국가 데이터 기준 2년치 동기화가 실행된다")
    void syncLastAndThisYearTest() {
        List<String> countryCodes = countryRepository.findAll().stream()
                .map(Country::getCountryCode)
                .toList();

        int thisYear = Year.now().getValue();
        int lastYear = thisYear - 1;

        // when
        holidaySchedulerService.syncLastAndThisYear();

        // then
        for (String countryCode : countryCodes) {
            verify(holidayUpsertService).upsert(lastYear, countryCode);
            verify(holidayUpsertService).upsert(thisYear, countryCode);
        }
        verify(holidayUpsertService, times(countryCodes.size() * 2)).upsert(anyInt(), anyString());
    }

}