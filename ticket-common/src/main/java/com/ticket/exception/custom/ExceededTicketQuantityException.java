package com.ticket.exception.custom;

import com.ticket.exception.ErrorCode;
import com.ticket.exception.ExceptionBase;
import org.springframework.http.HttpStatus;

public class ExceededTicketQuantityException extends ExceptionBase {

    private static final String MESSAGE_TEMPLATE = "[%s(%d)] 남은 티켓 수량보다 많은 티켓 수량을 요청할 수 없습니다.";

    public ExceededTicketQuantityException(final String ticketName, final Integer price) {
        this.errorCode = ErrorCode.EXCEEDED_TICKET_QUANTITY;
        this.errorMessage = String.format(MESSAGE_TEMPLATE, ticketName, price);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}