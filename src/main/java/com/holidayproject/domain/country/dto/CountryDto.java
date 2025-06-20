package com.holidayproject.domain.country.dto;

import com.holidayproject.domain.country.entity.Country;

public record CountryDto(
        String countryCode,
        String name
) {
    public static CountryDto of(Country country) {
        return new CountryDto(country.getCountryCode(), country.getName());
    }
}
