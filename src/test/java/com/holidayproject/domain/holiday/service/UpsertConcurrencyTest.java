package com.holidayproject.domain.holiday.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.holidayproject.IntegrationTestSupport;
import com.holidayproject.domain.holiday.entity.Holiday;
import com.holidayproject.domain.holiday.repository.HolidayRepository;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UpsertConcurrencyTest extends IntegrationTestSupport {

    private static final int THREAD_COUNT = 30;

    @Autowired
    private HolidayUpsertService holidayUpsertService;
    @Autowired
    private HolidayRepository holidayRepository;

    @AfterEach
    void cleanUp() {
        holidayRepository.deleteAllInBatch();
    }

    @DisplayName("30개의 쓰레드가 동시에 upsert를 실행해도 중복 저장되지 않는다")
    @Test
    void concurrentUpsertTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    holidayUpsertService.upsert(2025, "US");
                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        List<Holiday> holidays = holidayRepository.findAllByYearAndCountryCode(2025, "US");
        assertThat(holidays).hasSize(16) // 실제 API 1회 호출 결과 16개
                .extracting("countryCode", "year")
                .containsOnly(tuple("US", 2025));
    }
}
