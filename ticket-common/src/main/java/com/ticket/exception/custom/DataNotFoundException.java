package com.ticket.exception.custom;

import com.ticket.exception.ErrorCode;
import com.ticket.exception.ExceptionBase;
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
