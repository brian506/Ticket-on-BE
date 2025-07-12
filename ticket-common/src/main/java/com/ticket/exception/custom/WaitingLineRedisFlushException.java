package com.ticket.exception.custom;

import com.ticket.exception.ErrorCode;
import com.ticket.exception.ExceptionBase;
import org.springframework.http.HttpStatus;

public class WaitingLineRedisFlushException extends ExceptionBase {

    public WaitingLineRedisFlushException(String message) {
        this.errorCode = ErrorCode.INVALID_TICKET_CANCELLATION;
        this.errorMessage = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}