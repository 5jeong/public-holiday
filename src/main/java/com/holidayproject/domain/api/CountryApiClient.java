package com.holidayproject.domain.api;

import com.holidayproject.domain.country.dto.CountryDto;
import com.holidayproject.global.exception.BusinessException;
import com.holidayproject.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class CountryApiClient {

    private static final String COUNTRY_URL = "https://date.nager.at/api/v3/AvailableCountries";
    private final RestClient restClient;

    public List<CountryDto> getAvailableCountries() {
        return restClient.get()
                .uri(COUNTRY_URL)
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response)->{
                    throw new BusinessException(ErrorCode.NAGER_DATE_API_ERROR);
                })
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
