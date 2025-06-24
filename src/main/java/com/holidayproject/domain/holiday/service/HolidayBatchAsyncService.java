package com.holidayproject.domain.holiday.service;

import com.holidayproject.domain.holiday.dto.response.SuccessMessageResponse;
import com.holidayproject.global.component.HolidayBatchExecutor;
import java.time.Year;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayBatchAsyncService {

    public static final String SAVE_SUCCESSS_MESSAGE = "모든 국가 최근 5년치 공휴일 데이터 적재 완료";

    private final HolidaySaveService holidaySaveService;
    private final HolidayBatchExecutor batchExecutor;

    public SuccessMessageResponse saveHolidaysForFiveRecentYears() {

        List<Integer> years = getYears();

        batchExecutor.execute(years, holidaySaveService::saveHoliday);

        log.info("{}~{} 모든 국가 공휴일 데이터 적재 완료", years.getFirst(), years.getLast());

        return SuccessMessageResponse.of(SAVE_SUCCESSS_MESSAGE);
    }

    private List<Integer> getYears() {
        int currentYear = Year.now().getValue();
        return IntStream.rangeClosed(currentYear - 4, currentYear)
                .boxed()
                .toList();
    }

}
