package com.ticketon.ticketon.exception;

public enum ErrorResponseCode {
    NOT_VALID_TOKEN(4031),
    DATA_NOT_FOUND(4041);


    private final int code;

    ErrorResponseCode(int c) {
        this.code = c;
    }

    public int getCode() {
        return this.code;
    }
}
