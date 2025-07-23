package com.ticketon.ticketon.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.huxhorn.sulky.ulid.ULID;
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

    private String orderId;
    private Integer amount;
    private String paymentKey;
    private OffsetDateTime requestedAt;
    private OffsetDateTime approvedAt;

    public PaymentMessage fromResponse(PaymentConfirmResponse response,PaymentConfirmRequest request) {
        return PaymentMessage.builder()
                .ticketTypeId(request.getTicketTypeId())
                .paymentKey(response.getPaymentKey())
                .memberId(request.getMemberId())
                .orderId(response.orderId)
                .amount(response.getAmount())
                .requestedAt(LocalDateTime.from(response.getRequestedAt()))
                .approvedAt(response.getApprovedAt().toLocalDateTime())
                .build();
    }
}
