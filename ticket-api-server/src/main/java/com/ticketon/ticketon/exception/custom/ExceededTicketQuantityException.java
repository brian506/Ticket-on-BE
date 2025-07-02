package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.exception.ErrorCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public class ExceededTicketQuantityException extends ExceptionBase {

    private static final String MESSAGE_TEMPLATE = "[%s(%d)] 남은 티켓보다 수량보다 많은 티켓 수량을 요청할 수 없습니다.";

    public ExceededTicketQuantityException(@Nullable TicketType ticketType) {
        this.errorCode = ErrorCode.EXCEEDED_TICKET_QUANTITY;
        this.errorMessage = String.format(MESSAGE_TEMPLATE, ticketType.getName(), ticketType.getPrice());
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}