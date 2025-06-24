package com.holidayproject.global.component;

import com.holidayproject.domain.country.entity.Country;
import com.holidayproject.domain.country.repository.CountryRepository;
import com.holidayproject.global.exception.BusinessException;
import com.holidayproject.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HolidayBatchExecutor {

    public static final int BATCH_SIZE = 20;

    private final CountryRepository countryRepository;
    private final ThreadPoolTaskExecutor holidayExecutor;

    public void execute(List<Integer> years, BiConsumer<Integer, String> job) {
        List<String> countryCodes = getCountryCodes();
        List<List<String>> batches = splitList(countryCodes);

        for (List<String> batch : batches) {
            executeBatch(years, job, batch);
        }
    }

    private List<String> getCountryCodes() {
        return countryRepository.findAll().stream()
                .map(Country::getCountryCode)
                .toList();
    }

    private void executeBatch(List<Integer> years, BiConsumer<Integer, String> job, List<String> batch) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String countryCode : batch) {
            for (int year : years) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    executeJob(job, countryCode, year);
                }, holidayExecutor);
                futures.add(future);
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("배치 작업 완료 - 국가 {}개, 연도 {}", batch.size(), years);
    }

    private void executeJob(BiConsumer<Integer, String> job, String countryCode, int year) {
        try {
            job.accept(year, countryCode);
        } catch (Exception e) {
            log.error("[{} - {} 작업 실패", countryCode, year, e);
            throw new BusinessException(ErrorCode.BATCH_JOB_ERROR);
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
