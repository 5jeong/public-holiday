package com.holidayproject.domain.holiday.service;

import com.holidayproject.domain.api.HolidayApiClient;
import com.holidayproject.domain.holiday.dto.HolidayDto;
import com.holidayproject.domain.holiday.entity.Holiday;
import com.holidayproject.domain.holiday.repository.HolidayRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidaySaveService {

    private final HolidayApiClient holidayApiClient;
    private final HolidayRepository holidayRepository;

    @Transactional
    public void saveHoliday(int year, String countryCode) {

        List<HolidayDto> apiHolidays = holidayApiClient.getHolidays(year, countryCode);
        List<Holiday> existingHolidays = holidayRepository.findAllByYearAndCountryCode(year, countryCode);

        Set<String> existingKeys = existingHolidays.stream()
                .map(h -> h.getDate() + "|" + h.getCountryCode())
                .collect(Collectors.toSet());

        List<Holiday> newHolidays = apiHolidays.stream()
                .filter(dto -> !existingKeys.contains(dto.date() + "|" + dto.countryCode()))
                .map(Holiday::from)
                .toList();

        if (newHolidays.isEmpty()) {
            log.info("[{}-{}] 추가 공휴일 없음", countryCode, year);
            return;
        }

        log.info("[{}-{}] 신규 공휴일 {}건 저장", countryCode, year, newHolidays.size());
        holidayRepository.saveAll(newHolidays);
    }
}
