package com.ticketon.ticketon.exception.payment;

public class PaymentConfirmException extends RuntimeException {

    public PaymentConfirmException() {
        super();
    }
    public PaymentConfirmException(String message) {
        super(message);
    }
    public PaymentConfirmException(String message, Throwable cause) {
        super(message, cause);
    }
    public PaymentConfirmException(Throwable cause) {
        super(cause);
    }

}
