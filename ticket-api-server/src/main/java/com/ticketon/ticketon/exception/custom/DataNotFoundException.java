package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.exception.ErrorCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public class DataNotFoundException extends ExceptionBase {

    public DataNotFoundException(@Nullable String message) {
        this.errorCode = ErrorCode.DATA_NOT_FOUND;
        this.errorMessage = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
