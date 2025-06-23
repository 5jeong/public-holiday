package com.holidayproject.domain.holiday.dto.response;

public record SuccessMessageResponse(String message) {

    public static SuccessMessageResponse of(String message){
        return new SuccessMessageResponse(message);
    }
}
