package com.ticketon.ticketon.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true) // 외부 api 에서
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PaymentResponse {
    private String ticketId;
    private double amount;
    private String paymentKey;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
}
