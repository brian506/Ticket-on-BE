package com.ticketon.ticketon.domain.payment.dto;

import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentConfirmRequest {
    private Long memberId;
    private String paymentKey;
    private String ticketId; // 요청 보낼 때만 String 타입
    private int amount;

    public TossConfirmRequest toTossConfirmRequest() {
        return new TossConfirmRequest(
                this.paymentKey,
                this.getTicketId(),
                this.amount);
    }

    public Payment toEntity(PaymentConfirmRequest request, PaymentConfirmResponse response) {
        return Payment.builder()
                .ticketId(Long.valueOf(request.getTicketId()))
                .memberId(request.getMemberId())
                .paymentStatus(PaymentStatus.SUCCESS)
                .amount(request.getAmount())
                .paymentKey(request.paymentKey)
                .requestedAt(response.getRequestedAt().toLocalDateTime())
                .approvedAt(response.getApprovedAt().toLocalDateTime())
                .build();

    }
}
