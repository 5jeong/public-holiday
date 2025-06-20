package com.holidayproject.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.holidayproject.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private Boolean isSuccess;

    @JsonInclude(Include.NON_NULL)
    private String message;

    @JsonInclude(Include.NON_NULL)
    private T result;          // 요청 성공 시 반환되는 결과 데이터

    @JsonInclude(Include.NON_NULL)
    private Integer code;


    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(true, null, result, null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode code) {
        return new ApiResponse<>(false, code.getMessage(), null, code.getCode());
    }
}
