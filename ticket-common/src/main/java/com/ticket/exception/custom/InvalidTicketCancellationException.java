package com.ticket.exception.custom;

import com.ticket.exception.ErrorCode;
import com.ticket.exception.ExceptionBase;
import org.springframework.http.HttpStatus;

public class InvalidTicketCancellationException extends ExceptionBase {

    private static final String MESSAGE_TEMPLATE = "[%s(%d)] 발급된 티켓이 없습니다.";

    public InvalidTicketCancellationException(String ticketName, Long price) {
        this.errorCode = ErrorCode.INVALID_TICKET_CANCELLATION;
        this.errorMessage = String.format(MESSAGE_TEMPLATE, ticketName, price);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
