package com.ticketon.ticketon.domain.payment.dto;

import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.entity.PaymentStatus;
import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
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

    private Long ticketId;
    private Long ticketTypeId;
    private String paymentKey;
    private Long memberId;
    private String orderId;
    private Integer amount;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;
    private LocalDateTime expiredAt;

    public Payment toEntity(PaymentMessage message){
        return Payment.builder()
                .ticketId(message.getTicketId())
                .ticketTypeId(message.ticketTypeId)
                .orderId(message.getOrderId())
                .memberId(message.getMemberId())
                .amount(message.getAmount())
                .paymentKey(message.getPaymentKey())
                .requestedAt(message.requestedAt)
                .approvedAt(message.approvedAt)
                .canceledAt(message.getCanceledAt())
                .build();
    }

}
