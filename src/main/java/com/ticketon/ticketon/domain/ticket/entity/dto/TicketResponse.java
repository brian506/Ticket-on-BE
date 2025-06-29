package com.ticketon.ticketon.domain.ticket.entity.dto;

import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TicketResponse {

    private Long id;
    private Long ticketTypeId;
    private String ticketTypeName;
    private String eventTitle;
    private Long memberId;
    private String memberEmail;
    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;
    private int price;

    public static TicketResponse from(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketTypeId(ticket.getTicketType().getId())
                .ticketTypeName(ticket.getTicketType().getName())
                .eventTitle(ticket.getTicketType().getEventItem().getTitle())
                .memberId(ticket.getMember().getId())
                .memberEmail(ticket.getMember().getEmail())
                .ticketStatus(ticket.getTicketStatus())
                .build();
    }

}
