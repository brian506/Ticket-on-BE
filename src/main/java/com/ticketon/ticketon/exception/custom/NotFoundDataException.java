package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.exception.ErrorResponseCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;


//데이터 조회 실패 Exception
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
