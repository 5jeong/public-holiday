package com.holidayproject.domain.holiday.entity;

import com.holidayproject.domain.holiday.dto.HolidayDto;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "holiday_year")
    private int year;
    private LocalDate date;
    private String localName;
    private String name;
    private String countryCode;
    private boolean fixed;
    private boolean global;
    private Integer launchYear;

    @ElementCollection
    @CollectionTable(name = "holiday_counties", joinColumns = @JoinColumn(name = "holiday_id"))
    @Column(name = "county")
    private List<String> counties;

    @ElementCollection
    @CollectionTable(name = "holiday_types", joinColumns = @JoinColumn(name = "holiday_id"))
    @Column(name = "type")
    private List<String> types;

    @Builder
    public Holiday(Long id, int year, LocalDate date, String localName, String name, String countryCode, boolean fixed,
                   boolean global, Integer launchYear, List<String> counties, List<String> types) {
        this.id = id;
        this.year = year;
        this.date = date;
        this.localName = localName;
        this.name = name;
        this.countryCode = countryCode;
        this.fixed = fixed;
        this.global = global;
        this.launchYear = launchYear;
        this.counties = counties;
        this.types = types;
    }

    public static Holiday from(HolidayDto dto) {
        return Holiday.builder()
                .year(dto.date().getYear())
                .date(dto.date())
                .localName(dto.localName())
                .name(dto.name())
                .countryCode(dto.countryCode())
                .fixed(dto.fixed())
                .global(dto.global())
                .launchYear(dto.launchYear())
                .counties(dto.counties())
                .types(dto.types())
                .build();
    }

}
