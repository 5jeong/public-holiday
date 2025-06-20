package com.holidayproject.domain.country.service;

import com.holidayproject.domain.api.CountryApiClient;
import com.holidayproject.domain.country.dto.CountryDto;
import com.holidayproject.domain.country.entity.Country;
import com.holidayproject.domain.country.repository.CountryRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService {

    private final CountryApiClient countryApiClient;
    private final CountryRepository countryRepository;

    @Transactional
    public void initCountries() {
        List<CountryDto> availableCountries = countryApiClient.getAvailableCountries();

        Set<String> existingCodes = countryRepository.findAll().stream()
                .map(Country::getCountryCode)
                .collect(Collectors.toSet());

        List<Country> newCountries = availableCountries.stream()
                .filter(dto -> !existingCodes.contains(dto.countryCode()))
                .map(Country::from)
                .toList();

        if (isNewCountries(newCountries)) {
            log.info("신규 국가 {}건 저장", newCountries.size());
            countryRepository.saveAll(newCountries);
        }
    }

    private boolean isNewCountries(List<Country> newCountries) {
        return !newCountries.isEmpty();
    }
}
