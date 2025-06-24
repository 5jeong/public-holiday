package com.holidayproject.domain.api;

import com.holidayproject.domain.country.dto.CountryDto;
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
public class CountryApiClient {

    private static final String COUNTRY_URL = "https://date.nager.at/api/v3/AvailableCountries";
    private final RestClient restClient;

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public List<CountryDto> getAvailableCountries() {
        return restClient.get()
                .uri(COUNTRY_URL)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new BusinessException(ErrorCode.NAGER_DATE_API_ERROR);
                })
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Recover
    public List<CountryDto> recover(Exception e) {
        log.error("[nager API 실패] 국가 목록 조회 실패 : {}", e.getMessage());
        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
