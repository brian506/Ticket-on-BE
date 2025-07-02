package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.exception.ErrorCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public class InvalidTicketCancellationException extends ExceptionBase {

    private static final String MESSAGE_TEMPLATE = "[%s(%d)] 발급된 티켓이 없습니다.";

    //todo 여기서도 객체에 대한 의존성이 생겨버림, 차라리 에러 메시지를 외부에서 만들어서 주입하는게 좋을듯
    public InvalidTicketCancellationException(@Nullable TicketType ticketType) {
        this.errorCode = ErrorCode.INVALID_TICKET_CANCELLATION;
        this.errorMessage = String.format(MESSAGE_TEMPLATE, ticketType.getName(), ticketType.getPrice());
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
