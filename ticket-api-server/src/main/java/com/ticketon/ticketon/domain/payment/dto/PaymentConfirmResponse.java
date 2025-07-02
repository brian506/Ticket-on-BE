package com.ticketon.ticketon.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true) // 외부 api 에서
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PaymentConfirmResponse {
    private String ticketTypeId;
    private int amount;
    private String paymentKey;
    private OffsetDateTime requestedAt;
    private OffsetDateTime approvedAt;

    public PaymentMessage fromResponse(PaymentConfirmResponse response) {
        return PaymentMessage.builder()
                .paymentKey(response.getPaymentKey())
                .ticketTypeId(Long.valueOf(response.getTicketTypeId()))
                //.memberId(// 현재 로그인된 사용자))
                .amount(response.getAmount())
                .requestedAt(LocalDateTime.from(response.getRequestedAt()))
                .approvedAt(response.getApprovedAt().toLocalDateTime())
                .build();
    }
}
