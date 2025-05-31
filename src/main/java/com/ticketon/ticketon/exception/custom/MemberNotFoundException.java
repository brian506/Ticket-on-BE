package com.ticketon.ticketon.exception.custom;


import com.ticketon.ticketon.exception.ErrorResponseCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends ExceptionBase {

    public MemberNotFoundException(@Nullable String message) {
        this.errorCode = ErrorResponseCode.DATA_NOT_FOUND;
        this.errorMessage = "[" + message + "] email의 member를 찾을 수 없습니다";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
