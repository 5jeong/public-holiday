package com.holidayproject.domain.api;

import com.holidayproject.domain.holiday.dto.HolidayDto;
import com.holidayproject.global.exception.BusinessException;
import com.holidayproject.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class HolidayApiClient {

    private static final String PUBLIC_HOLIDAY_URL = "https://date.nager.at/api/v3/PublicHolidays/%d/%s";
    private final RestClient restClient;

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public List<HolidayDto> getHolidays(int year, String countryCode) {
        return restClient.get()
                .uri(String.format(PUBLIC_HOLIDAY_URL, year, countryCode))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new BusinessException(ErrorCode.NAGER_DATE_API_ERROR);
                })
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Recover
    public List<HolidayDto> recover(Exception e, int year, String countryCode) {
        log.error("[nager API 실패] 공휴일 조회 실패 - year: {}, countryCode: {}, 이유 : {}", year, countryCode, e.getMessage());
        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
