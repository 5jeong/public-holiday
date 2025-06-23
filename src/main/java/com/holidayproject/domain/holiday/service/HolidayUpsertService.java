package com.holidayproject.domain.holiday.service;

import com.holidayproject.domain.api.HolidayApiClient;
import com.holidayproject.domain.holiday.entity.Holiday;
import com.holidayproject.domain.holiday.repository.HolidayRepository;
import com.holidayproject.global.component.DistributeLockExecutor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayUpsertService {

    public static final String HOLIDAY_LOCK_NAME = "lock:holiday:%d:%s";

    private final DistributeLockExecutor distributeLockExecutor;
    private final HolidayApiClient holidayApiClient;
    private final HolidayDeleteService holidayDeleteService;
    private final HolidayRepository holidayRepository;

    public void upsert(int year, String countryCode) {
        String lockName = String.format(HOLIDAY_LOCK_NAME, year, countryCode);
        distributeLockExecutor.execute(
                lockName, 5000, 5000, () -> refreshHolidays(year, countryCode)
        );
    }

    @Transactional
    public void refreshHolidays(int year, String countryCode) {
        // 기존 데이터 삭제
        holidayDeleteService.deleteByYearAndCountry(year, countryCode);

        // 외부api 호출
        List<Holiday> newHolidays = holidayApiClient.getHolidays(year, countryCode).stream()
                .map(Holiday::from)
                .toList();

        // 다시 저장
        holidayRepository.saveAll(newHolidays);

        log.info("연도: {}, 국가: {} 공휴일 {}건 업데이트 완료", year, countryCode, newHolidays.size());

    }
}
