package com.holidayproject.domain.holiday.service;

import com.holidayproject.domain.api.HolidayApiClient;
import com.holidayproject.domain.holiday.dto.HolidayDto;
import com.holidayproject.domain.holiday.entity.Holiday;
import com.holidayproject.domain.holiday.repository.HolidayRepository;
import java.time.LocalDate;
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
        Set<String> existingKeys = getExistingHolidayKeys(year, countryCode);

        List<Holiday> newHolidays = filterNewHolidays(apiHolidays, existingKeys);

        if (newHolidays.isEmpty()) {
            log.info("[{}-{}] 추가 공휴일 없음", countryCode, year);
            return;
        }

        log.info("[{}-{}] 신규 공휴일 {}건 저장", countryCode, year, newHolidays.size());
        holidayRepository.saveAll(newHolidays);
    }

    private Set<String> getExistingHolidayKeys(int year, String countryCode) {
        return holidayRepository.findAllByYearAndCountryCode(year, countryCode).stream()
                .map(h -> generateKey(h.getDate(), h.getCountryCode()))
                .collect(Collectors.toSet());
    }

    private List<Holiday> filterNewHolidays(List<HolidayDto> dtos, Set<String> existingKeys) {
        return dtos.stream()
                .filter(dto -> !existingKeys.contains(generateKey(dto.date(), dto.countryCode())))
                .map(Holiday::from)
                .toList();
    }

    private String generateKey(LocalDate date, String countryCode) {
        return date + "|" + countryCode;
    }

}
