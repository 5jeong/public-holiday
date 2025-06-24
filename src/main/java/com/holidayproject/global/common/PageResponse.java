package com.holidayproject.global.common;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageResponse<T> {
    private final List<T> content;        // 실제 데이터
    private final int page;               // 현재 페이지 (1부터 시작)
    private final int size;               // 페이지 크기
    private final long totalElements;     // 전체 요소 수
    private final int totalPages;         // 전체 페이지 수
    private final boolean first;          // 첫 페이지 여부
    private final boolean last;           // 마지막 페이지 여부

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber() + 1;
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
    }
}