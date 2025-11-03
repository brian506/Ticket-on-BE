package com.ticketon.ticketon.domain.ticket.dto;

import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TicketReadyResponse {
    private Long ticketId;
    private Long ticketTypeId;
    private Long memberId;
    private TicketStatus ticketStatus;
    private Integer price;
    private String orderId;
    private LocalDateTime expiredAt;

    public static TicketReadyResponse toDto(Ticket ticket,String orderId){
        return TicketReadyResponse.builder()
                .ticketId(ticket.getId())
                .ticketTypeId(ticket.getTicketTypeId())
                .memberId(ticket.getMemberId())
                .ticketStatus(ticket.getTicketStatus())
                .price(ticket.getPrice())
                .orderId(orderId)
                .expiredAt(ticket.getExpiredAt())
                .build();

    }
}
