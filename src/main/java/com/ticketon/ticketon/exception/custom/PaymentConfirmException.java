package com.ticketon.ticketon.exception.custom;

import com.ticketon.ticketon.exception.ErrorCode;
import com.ticketon.ticketon.exception.ExceptionBase;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public class PaymentConfirmException extends ExceptionBase {

    public PaymentConfirmException(@Nullable String message) {
        this.errorCode = ErrorCode.PAYMENT_CONFIRM;
        this.errorMessage = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
