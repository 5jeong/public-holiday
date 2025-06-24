package com.holidayproject.global.scheduler;

import com.holidayproject.domain.country.entity.Country;
import com.holidayproject.domain.country.repository.CountryRepository;
import com.holidayproject.domain.holiday.service.HolidayUpsertService;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidaySchedulerService {

    public static final int BATCH_SIZE = 20;

    private final CountryRepository countryRepository;
    private final HolidayUpsertService holidayUpsertService;
    private final ThreadPoolTaskExecutor holidayExecutor;

    public void syncLastAndThisYear() {
        int thisYear = Year.now().getValue();
        int lastYear = thisYear - 1;

        List<String> countryCodes = countryRepository.findAll().stream()
                .map(Country::getCountryCode)
                .toList();

        List<List<String>> countryBatches = splitList(countryCodes);

        log.info("연도 {}~{} 모든 국가 동기화 시작", lastYear, thisYear);
        for (List<String> countryBatch : countryBatches) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (String countryCode : countryBatch) {
                CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
                    upsertHoliday(countryCode, lastYear);
                }, holidayExecutor);

                CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
                    upsertHoliday(countryCode, thisYear);
                }, holidayExecutor);

                futures.addAll(List.of(f1, f2));
            }

            // 현재 배치 작업 완료 대기
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info("연도 {}~{} 국가 {}개 동기화 완료", lastYear, thisYear, countryBatch.size());
        }

        log.info("연도 {}~{} 모든 국가 동기화 완료", lastYear, thisYear);

    }

    private void upsertHoliday(String countryCode, int lastYear) {
        try {
            holidayUpsertService.upsert(lastYear, countryCode);
        } catch (Exception e) {
            log.error("[{}-{}] 동기화 실패", countryCode, lastYear, e);
        }
    }

    private <T> List<List<T>> splitList(List<T> list) {
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += BATCH_SIZE) {
            chunks.add(list.subList(i, Math.min(i + BATCH_SIZE, list.size())));
        }
        return chunks;
    }
}
