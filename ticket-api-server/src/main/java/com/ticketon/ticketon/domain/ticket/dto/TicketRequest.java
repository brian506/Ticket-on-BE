package com.ticketon.ticketon.domain.ticket.dto;

import com.ticketon.ticketon.domain.ticket.entity.TicketStatus;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import jakarta.validation.constraints.Max;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {

    private Long ticketTypeId;
    @Max(1)
    private Integer quantity;
    private String orderId;
    private Long memberId;
    private Integer amount;

    public static TicketRequest from(Long memberId, TicketType ticketType) {
        return TicketRequest.builder()
                .ticketTypeId(ticketType.getId())
                .memberId(memberId)
                .amount(Math.toIntExact(ticketType.getPrice()))
                .build();
    }

    public NewTicketEvent toEvent(String orderId) {
        return NewTicketEvent.builder()
                .ticketTypeId(this.ticketTypeId)
                .memberId(this.memberId)
                .orderId(orderId)
                .price(this.amount)
                .build();

    }
}
