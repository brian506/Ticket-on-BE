package com.ticketon.ticketon.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    EXCEEDED_TICKET_QUANTITY(4001),
    INVALID_TICKET_CANCELLATION(4002),
    INVALID_MESSAGE(4003),
    NOT_VALID_TOKEN(4031),
    DATA_NOT_FOUND(4041),
    TICKET_ALREADY_CANCELLED(4091),
    PAYMENT_CONFIRM(5008),
    PAYMENT_CANCELLED(5009);


    private final int code;
}
