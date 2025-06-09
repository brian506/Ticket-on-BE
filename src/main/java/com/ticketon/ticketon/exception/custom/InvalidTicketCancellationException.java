package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.exception.ErrorResponseCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public class InvalidTicketCancellationException extends ExceptionBase {
    public InvalidTicketCancellationException(@Nullable TicketType ticketType) {
        this.errorCode = ErrorResponseCode.INVALID_TICKET_CANCELLATION;
        this.errorMessage = "[" + ticketType.getName() + "(" + ticketType.getPrice() + ")" + "] 발급된 티켓이 없습니다.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
