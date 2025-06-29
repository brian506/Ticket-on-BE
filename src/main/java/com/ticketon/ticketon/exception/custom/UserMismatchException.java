package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.exception.ErrorCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import org.springframework.http.HttpStatus;

public class UserMismatchException extends ExceptionBase {

    private static final String MESSAGE_TEMPLATE = "사용자 정보가 일치하지 않습니다 [sessionUser=%s, requestUser=%s]";

    public UserMismatchException(String session, String requestUser) {
        this.errorCode = ErrorCode.EXCEEDED_TICKET_QUANTITY;
        this.errorMessage = String.format(MESSAGE_TEMPLATE, session, requestUser);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
