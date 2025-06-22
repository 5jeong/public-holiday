package com.holidayproject.domain.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.holidayproject.domain.holiday.dto.HolidayDto;
import com.holidayproject.global.exception.BusinessException;
import com.holidayproject.global.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

class HolidayApiClientTest {
    private RestClient restClient;
    private MockRestServiceServer mockServer;
    private HolidayApiClient holidayApiClient;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        this.mockServer = MockRestServiceServer.createServer(restTemplate);
        this.restClient = RestClient.create(restTemplate);
        this.holidayApiClient = new HolidayApiClient(restClient);
    }

    @DisplayName("공휴일 API가 정상 응답 시 리스트를 반환한다")
    @Test
    void getHolidaysTest1() {
        // given
        int year = 2025;
        String countryCode = "KR";
        String url = "https://date.nager.at/api/v3/PublicHolidays/2025/KR";

        mockServer.expect(requestTo(url))
                .andExpect(method(GET))
                .andRespond(withSuccess("""
                        [
                          {
                            "date": "2025-01-01",
                            "localName": "새해",
                            "name": "New Year's Day",
                            "countryCode": "KR",
                            "fixed": false,
                            "global": true,
                            "counties": null,
                            "launchYear": null,
                            "types": ["Public"]
                          }
                        ]
                        """, APPLICATION_JSON));

        // when
        List<HolidayDto> result = holidayApiClient.getHolidays(year, countryCode);

        // then
        assertThat(result).hasSize(1)
                .extracting("date", "localName", "name", "countryCode", "fixed", "global")
                .containsExactly(
                        tuple(LocalDate.of(2025, 1, 1), "새해", "New Year's Day", "KR", false, true)
                );
    }

    @DisplayName("공휴일 API가 오류 응답일 경우 BusinessException이 발생한다")
    @Test
    void getHolidaysTest2() {
        // given
        int year = 2025;
        String countryCode = "KR";
        String url = "https://date.nager.at/api/v3/PublicHolidays/2025/KR";

        mockServer.expect(requestTo(url))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        // when & then
        assertThatThrownBy(() -> holidayApiClient.getHolidays(year, countryCode))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NAGER_DATE_API_ERROR.getMessage());
    }

}