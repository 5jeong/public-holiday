package com.holidayproject.domain.country.entity;

import com.holidayproject.domain.country.dto.CountryDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Country {
    @Id
    private String countryCode;

    private String name;

    @Builder
    public Country(String countryCode, String name) {
        this.countryCode = countryCode;
        this.name = name;
    }

    public static Country from(CountryDto dto) {
        return new Country(dto.countryCode(), dto.name());
    }
}
