package com.ticketon.ticketon.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ExceptionBase extends RuntimeException {
    protected String errorMessage;
    protected ErrorCode errorCode;

    public abstract HttpStatus getHttpStatus();
}