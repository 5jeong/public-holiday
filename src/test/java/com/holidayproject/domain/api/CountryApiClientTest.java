package com.holidayproject.domain.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.holidayproject.domain.country.dto.CountryDto;
import com.holidayproject.global.exception.BusinessException;
import com.holidayproject.global.exception.ErrorCode;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("test")
class CountryApiClientTest {
    private RestClient restClient;
    private MockRestServiceServer mockServer;
    private CountryApiClient countryApiClient;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        this.mockServer = MockRestServiceServer.createServer(restTemplate);
        this.restClient = RestClient.create(restTemplate);
        this.countryApiClient = new CountryApiClient(restClient);
    }


    @DisplayName("국가목록 API가 정상 응답 시 리스트를 반환한다")
    @Test
    void CountryApiClientTest1() {
        // given
        mockServer.expect(requestTo("https://date.nager.at/api/v3/AvailableCountries"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                            [
                              {"countryCode": "KR", "name": "Korea"},
                              {"countryCode": "US", "name": "United States"}
                            ]
                        """, MediaType.APPLICATION_JSON));
        // when
        List<CountryDto> result = countryApiClient.getAvailableCountries();

        // then
        assertThat(result).hasSize(2);
    }

    @DisplayName("국가목록 API가 오류 응답일 경우 BussinessException이 발생한다")
    @Test
    void CountryApiClientTest2() {
        // given
        mockServer.expect(requestTo("https://date.nager.at/api/v3/AvailableCountries"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        // when & then
        assertThatThrownBy(() -> countryApiClient.getAvailableCountries()).isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NAGER_DATE_API_ERROR.getMessage());

    }
}