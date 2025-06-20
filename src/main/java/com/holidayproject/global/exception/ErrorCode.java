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
    NAGER_DATE_API_ERROR(HttpStatus.BAD_REQUEST,1002,"Nager Date API 오류입니다.");

    // 2000번대 - 공휴일 관련 에러

    private final HttpStatus status; // HTTP 상태 코드
    private final int code; // 오류 코드
    private final String message; // 오류 메시지
}
