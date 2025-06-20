package com.holidayproject.domain.country;

import com.holidayproject.domain.country.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CountryInitializer implements ApplicationRunner {

    private final CountryService countryService;

    @Override
    public void run(ApplicationArguments args){
        log.info("국가 정보 초기화 시작");
        countryService.initCountries();
        log.info("국가 정보 초기화 완료");
    }
}
