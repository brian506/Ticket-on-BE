package com.ticketon.ticketon.domain.payment.dto;

import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PaymentResponse {

    private Long id;
    private Long ticketTypeId;
    private Long memberId;
    private String orderId;
    private PaymentStatus status;
    private Integer amount;
    private LocalDateTime approvedAt;

    public static PaymentResponse toDto(Payment payment){
        return PaymentResponse.builder()
                .id(payment.getId())
                .memberId(payment.getMemberId())
                .orderId(payment.getOrderId())
                .status(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .approvedAt(payment.getApprovedAt())
                .build();
    }
}
