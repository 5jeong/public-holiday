package com.holidayproject.global.exception;

import com.holidayproject.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBusinessException(final BusinessException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.fail(e.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(final Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}