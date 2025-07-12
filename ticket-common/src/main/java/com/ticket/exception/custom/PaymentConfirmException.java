package com.ticket.exception.custom;

import com.ticket.exception.ErrorCode;
import com.ticket.exception.ExceptionBase;
import org.springframework.http.HttpStatus;

public class PaymentConfirmException extends ExceptionBase {

    private static final String MESSAGE_TEMPLATE = "토스 결제 확인을 실패했습니다.";

    public PaymentConfirmException() {
        this.errorCode = ErrorCode.INVALID_TICKET_CANCELLATION;
        this.errorMessage = MESSAGE_TEMPLATE;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
