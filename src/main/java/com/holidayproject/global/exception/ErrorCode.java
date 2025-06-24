package com.holidayproject.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 1000번대 - 공통 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1000, "예상치 못한 서버 오류입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 1001, "잘못된 값을 입력했습니다."),
    NAGER_DATE_API_ERROR(HttpStatus.BAD_REQUEST, 1002, "Nager Date API 오류입니다."),
    LOCK_ACQUIRE_FAIL(HttpStatus.CONFLICT, 1003, "공휴일 데이터에 대한 락 획득에 실패했습니다."),
    BATCH_JOB_ERROR(HttpStatus.CONFLICT, 1004, "배치 작업 처리중 예외가 발생했습니다."),

    // 2000번대 - 공휴일 관련 로직 에러
    INVALID_SEARCH_CONDITION(HttpStatus.BAD_REQUEST, 2001, "year와 from/to는 동시에 검색할 수 없습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, 2002, "from 날짜는 to보다 이전이어야 합니다."),
    INVALID_COUNTRY_CODE(HttpStatus.NOT_FOUND, 2003, "존재하지않는 나라코드입니다."),
    INVALID_YEAR(HttpStatus.BAD_REQUEST, 2004, "지원하지 않는 연도입니다. (1975 ~ 2075)"),
    NO_HOLIDAY_TO_DELETE(HttpStatus.BAD_REQUEST, 2005, "삭제할 공휴일이 존재하지 않습니다.");
    private final HttpStatus status; // HTTP 상태 코드
    private final int code; // 오류 코드
    private final String message; // 오류 메시지
}
