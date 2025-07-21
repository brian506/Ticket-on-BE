package com.ticketon.ticketon.domain.payment.dto;

import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMessage {

    private Long ticketTypeId;
    private String paymentKey;
    private Long memberId;
    private String orderId;
    private int amount;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;

    public Payment toEntity(PaymentMessage message){
        return Payment.builder()
                .ticketTypeId(message.getTicketTypeId())
                .orderId(message.getOrderId())
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
