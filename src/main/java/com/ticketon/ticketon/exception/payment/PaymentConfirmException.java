package com.ticketon.ticketon.exception.payment;

import com.ticketon.ticketon.exception.ExceptionBase;
import org.springframework.http.HttpStatus;

public class PaymentConfirmException extends ExceptionBase {

    public PaymentConfirmException(String code) {
        this.errorCode = PaymentResponseErrorCode.findByCode(code);
    }
    @Override
    public HttpStatus getHttpStatus() {
        return ((PaymentResponseErrorCode) errorCode).getHttpStatus();
    }
}
