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

        Set<String> existingCodes = getExistingCodes();

        List<Country> newCountries = filterNewCountries(availableCountries, existingCodes);

        if (isNewCountries(newCountries)) {
            log.info("신규 국가 {}건 저장", newCountries.size());
            countryRepository.saveAll(newCountries);
        }
    }

    private List<Country> filterNewCountries(List<CountryDto> availableCountries, Set<String> existingCodes) {
        return availableCountries.stream()
                .filter(dto -> !existingCodes.contains(dto.countryCode()))
                .map(Country::from)
                .toList();
    }

    private Set<String> getExistingCodes() {
        return countryRepository.findAll().stream()
                .map(Country::getCountryCode)
                .collect(Collectors.toSet());
    }

    private boolean isNewCountries(List<Country> newCountries) {
        return !newCountries.isEmpty();
    }
}
