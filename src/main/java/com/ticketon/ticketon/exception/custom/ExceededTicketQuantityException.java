package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.exception.ErrorResponseCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public class ExceededTicketQuantityException extends ExceptionBase {

    public ExceededTicketQuantityException(@Nullable TicketType ticketType) {
        this.errorCode = ErrorResponseCode.EXCEEDED_TICKET_QUANTITY;
        this.errorMessage = "[" + ticketType.getName() + "(" + ticketType.getPrice() + ")" + "] 남은 티켓보다 수량보다 많은 티켓 수량을 요청할 수 없습니다.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
