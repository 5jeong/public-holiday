package com.holidayproject.global.scheduler;

import com.holidayproject.domain.holiday.service.HolidayUpsertService;
import com.holidayproject.global.component.HolidayBatchExecutor;
import java.time.Year;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidaySchedulerService {


    private final HolidayUpsertService holidayUpsertService;
    private final HolidayBatchExecutor batchExecutor;

    public void syncLastAndThisYear() {

        List<Integer> years = getYears();
        batchExecutor.execute(years, holidayUpsertService::upsert);

        log.info("{}~{} 모든 국가 동기화 완료", years.getFirst(), years.getLast());
    }

    private List<Integer> getYears() {
        int thisYear = Year.now().getValue();
        int lastYear = thisYear - 1;
        return List.of(lastYear, thisYear);
    }
}
