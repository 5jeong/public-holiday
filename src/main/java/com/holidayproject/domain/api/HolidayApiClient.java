package com.holidayproject.domain.api;

import com.holidayproject.domain.holiday.dto.HolidayDto;
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
public class HolidayApiClient {

    private static final String PUBLIC_HOLIDAY_URL = "https://date.nager.at/api/v3/PublicHolidays/%d/%s";
    private final RestClient restClient;

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
}
