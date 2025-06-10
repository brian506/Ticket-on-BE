package com.ticketon.ticketon.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private Long userId;
    private String paymentKey;
    private Long ticketId; // 요청 보낼 때만 String 타입
    private double amount;
}
