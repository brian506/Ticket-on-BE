package com.ticketon.ticketon.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentFailResponse {
    private String code;
    private String message;
}
