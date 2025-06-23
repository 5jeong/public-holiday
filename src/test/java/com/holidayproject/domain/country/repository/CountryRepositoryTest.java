package com.holidayproject.domain.country.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.holidayproject.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CountryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private CountryRepository countryRepository;

    @Test
    @DisplayName("존재하는 국가 코드를 조회하면 true를 반환한다")
    void existsByCountryCode_true() {
        // given
        String countryCode = "KR";

        // when
        boolean exists = countryRepository.existsByCountryCode(countryCode);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 국가 코드를 조회하면 false를 반환한다")
    void existsByCountryCode_false() {
        // given
        String countryCode = "ZZZ";
        // when
        boolean exists = countryRepository.existsByCountryCode(countryCode);

        // then
        assertThat(exists).isFalse();
    }
}