package com.ticketon.ticketon.domain.payment.dto;

import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentMessage {
    private String paymentKey;
    private Long ticketId;
    private Long memberId;
    private int amount;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;

    public Payment toEntity(PaymentMessage message){
        return Payment.builder()
                .ticketId(message.getTicketId())
                .memberId(message.getMemberId())
                .paymentStatus(PaymentStatus.SUCCESS)
                .amount(message.getAmount())
                .paymentKey(message.getPaymentKey())
                .requestedAt(message.requestedAt)
                .approvedAt(message.approvedAt)
                .canceledAt(message.getCanceledAt())
                .build();
    }
}
