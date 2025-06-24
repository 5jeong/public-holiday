package com.holidayproject.global.scheduler;

import com.holidayproject.domain.holiday.service.HolidaySchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HolidaySyncScheduler {

    private final HolidaySchedulerService holidaySchedulerService;

    @Scheduled(cron = "0 0 1 2 1 *", zone = "Asia/Seoul")
    public void runYearlySync() {
        holidaySchedulerService.syncLastAndThisYear();
    }
}
