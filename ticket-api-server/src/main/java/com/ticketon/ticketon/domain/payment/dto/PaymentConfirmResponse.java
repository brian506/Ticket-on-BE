package com.ticketon.ticketon.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
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

    public PaymentMessage fromResponse(PaymentConfirmRequest request) {
        return PaymentMessage.builder()
                .ticketId(request.getTicketId())
                .ticketTypeId(request.getTicketTypeId())
                .paymentKey(this.paymentKey)
                .memberId(request.getMemberId())
                .orderId(request.getOrderId())
                .amount(this.amount)
                .requestedAt(LocalDateTime.from(this.requestedAt))
                .approvedAt(LocalDateTime.from(this.approvedAt))
                .build();
    }
}
