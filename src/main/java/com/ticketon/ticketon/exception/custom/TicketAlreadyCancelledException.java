package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.exception.ErrorResponseCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public class TicketAlreadyCancelledException extends ExceptionBase {

    public TicketAlreadyCancelledException() {
        this.errorCode = ErrorResponseCode.TICKET_ALREADY_CANCELLED;
        this.errorMessage = "이미 취소된 티켓입니다.";
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
