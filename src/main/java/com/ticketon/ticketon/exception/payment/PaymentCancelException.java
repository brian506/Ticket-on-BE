package com.ticketon.ticketon.exception.payment;


public class PaymentCancelException extends RuntimeException{
    public PaymentCancelException() {
        super();
    }
    public PaymentCancelException(String message) {
        super(message);
    }
    public PaymentCancelException(String message, Throwable cause) {
        super(message, cause);
    }
    public PaymentCancelException(Throwable cause) {
        super(cause);
    }

}
