package com.holidayproject.domain.country.repository;

import com.holidayproject.domain.country.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, String> {
    boolean existsByCountryCode(String countryCode);
}
