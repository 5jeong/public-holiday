package com.holidayproject.domain.holiday.repository;

import static com.holidayproject.domain.holiday.entity.QHoliday.holiday;

import com.holidayproject.domain.holiday.dto.request.HolidaySearchRequest;
import com.holidayproject.domain.holiday.dto.response.HolidayResponse;
import com.holidayproject.domain.holiday.entity.Holiday;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class HolidayRepositoryCustomImpl implements HolidayRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<HolidayResponse> searchHolidays(HolidaySearchRequest request, Pageable pageable) {
        List<Holiday> holidays = queryFactory
                .selectFrom(holiday)
                .where(applyAllFilters(request))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(holiday.date.asc())
                .fetch();

        List<HolidayResponse> content = holidays.stream()
                .map(HolidayResponse::of)
                .toList();

        JPAQuery<Long> countQuery = queryFactory.
                select(Wildcard.count)
                .from(holiday)
                .where(applyAllFilters(request));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanBuilder applyAllFilters(HolidaySearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(applyYear(request.year()));
        builder.and(applyFromDate(request.fromDate()));
        builder.and(applyToDate(request.toDate()));
        builder.and(applyName(request.name()));
        builder.and(applyLocalName(request.localName()));
        builder.and(applyCountryCode(request.countryCode()));
        builder.and(applyFixed(request.fixed()));
        builder.and(applyGlobal(request.global()));
        builder.and(applyLaunchYear(request.launchYear()));
        builder.and(applyCounties(request.counties()));
        builder.and(applyTypes(request.types()));

        return builder;
    }

    // 필드별 조건 메서드들
    private BooleanExpression applyYear(Integer year) {
        return year != null ? holiday.year.eq(year) : null;
    }

    private BooleanExpression applyFromDate(LocalDate from) {
        return from != null ? holiday.date.goe(from) : null;
    }

    private BooleanExpression applyToDate(LocalDate to) {
        return to != null ? holiday.date.loe(to) : null;
    }

    private BooleanExpression applyName(String name) {
        return StringUtils.hasText(name) ? holiday.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression applyLocalName(String localName) {
        return StringUtils.hasText(localName) ? holiday.localName.containsIgnoreCase(localName) : null;
    }

    private BooleanExpression applyCountryCode(String countryCode) {
        return StringUtils.hasText(countryCode) ? holiday.countryCode.eq(countryCode) : null;
    }

    private BooleanExpression applyFixed(Boolean fixed) {
        return fixed != null ? holiday.fixed.eq(fixed) : null;
    }

    private BooleanExpression applyGlobal(Boolean global) {
        return global != null ? holiday.global.eq(global) : null;
    }

    private BooleanExpression applyLaunchYear(Integer launchYear) {
        return launchYear != null ? holiday.launchYear.eq(launchYear) : null;
    }

    private BooleanExpression applyCounties(List<String> counties) {
        return (counties != null && !counties.isEmpty()) ? holiday.counties.any().in(counties) : null;
    }

    private BooleanExpression applyTypes(List<String> types) {
        return (types != null && !types.isEmpty()) ? holiday.types.any().in(types) : null;
    }

}
