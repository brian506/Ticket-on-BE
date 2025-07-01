package com.ticketon.ticketon.domain.ticket.dto;

import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketRequest {

    private Long ticketTypeId;
    private Long memberId;
    private int amount;

    public static TicketRequest from(Long memberId, TicketType ticketType) {
        return TicketRequest.builder()
                .ticketTypeId(ticketType.getId())
                .memberId(memberId)
                .amount(Math.toIntExact(ticketType.getPrice()))
                .build();
    }
}
