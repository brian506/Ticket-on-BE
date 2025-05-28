package com.ticketon.ticketon.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ExceptionBase extends RuntimeException {
    //HttpStatus
    public abstract HttpStatus getHttpStatus();
    //
    protected String errorMessage;
    protected ErrorResponseCode errorCode;
}