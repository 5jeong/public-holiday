package com.holidayproject.domain.holiday.service;

import com.holidayproject.domain.country.entity.Country;
import com.holidayproject.domain.country.repository.CountryRepository;
import com.holidayproject.domain.holiday.dto.response.SuccessMessageResponse;
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
public class HolidayBatchAsyncService {

    public static final int BATCH_SIZE = 20;
    public static final String SAVE_SUCCESSS_MESSAGE = "모든 국가 최근 5년치 공휴일 데이터 적재 완료";

    private final CountryRepository countryRepository;
    private final HolidaySaveService holidaySaveService;
    private final ThreadPoolTaskExecutor holidayExecutor;

    public SuccessMessageResponse saveHolidaysForFiveRecentYears() {
        int currentYear = Year.now().getValue();
        int startYear = currentYear - 4;

        List<String> countryCodes = countryRepository.findAll().stream()
                .map(Country::getCountryCode)
                .toList();

        List<List<String>> countryBatches = splitList(countryCodes);

        for (List<String> countryBatch : countryBatches) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int year = startYear; year <= currentYear; year++) {
                for (String countryCode : countryBatch) {
                    final int y = year;
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            holidaySaveService.saveHoliday(y, countryCode);
                        } catch (Exception e) {
                            log.error("[{}-{}] 처리 중 오류 발생", countryCode, y, e);
                        }
                    }, holidayExecutor);
                    futures.add(future);
                }
            }

            // 현재 배치 작업 완료 대기
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info("배치 작업 완료 (크기: {}, 연도당 총 작업 수: {})", countryBatch.size(),
                    (currentYear - startYear + 1) * countryBatch.size());
        }

        log.info("모든 국가 최근 5년치 공휴일 데이터 적재 완료");
        return SuccessMessageResponse.of(SAVE_SUCCESSS_MESSAGE);
    }

    private <T> List<List<T>> splitList(List<T> list) {
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += BATCH_SIZE) {
            chunks.add(list.subList(i, Math.min(i + BATCH_SIZE, list.size())));
        }
        return chunks;
    }

}
