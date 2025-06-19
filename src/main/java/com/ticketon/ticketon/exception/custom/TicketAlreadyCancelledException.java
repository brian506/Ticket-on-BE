package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.exception.ErrorCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import org.springframework.http.HttpStatus;

public class TicketAlreadyCancelledException extends ExceptionBase {

    private static final String ALREADY_CANCEL_TICKET = "이미 취소된 티켓입니다.";

    public TicketAlreadyCancelledException() {
        this.errorCode = ErrorCode.TICKET_ALREADY_CANCELLED;
        this.errorMessage = ALREADY_CANCEL_TICKET;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
