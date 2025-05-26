package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.exception.ErrorResponseCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//데이터 조회 실패 Exception
@ResponseStatus(value = HttpStatus.NOT_FOUND) // HTTP 응답 코드
public class NotFoundDataException extends ExceptionBase {

    public NotFoundDataException(@Nullable String message) {
        this.errorCode = ErrorResponseCode.DATA_NOT_FOUND;
        this.errorMessage = message;
    }

    @Override
    public int getStatusCode() {
        return errorCode.getCode();
    }
}
