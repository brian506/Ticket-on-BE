package com.ticket.exception.custom;

import com.ticket.exception.ErrorCode;
import com.ticket.exception.ExceptionBase;
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
