package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.exception.ErrorResponseCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public class NotFoundDataException extends ExceptionBase {

    public NotFoundDataException(@Nullable String message) {
        this.errorCode = ErrorResponseCode.DATA_NOT_FOUND;
        this.errorMessage = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
