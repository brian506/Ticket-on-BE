package com.ticketon.ticketon.domain.wating_queue.dto;

import lombok.Getter;

@Getter
public class BookingResponse {
    public boolean success;
    public String message;
    public BookingResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
