package com.ticketon.ticketon.domain.ticket.dto;

import com.ticketon.ticketon.domain.ticket.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketPayload {
    private String orderId;
    private Long ticketTypeId;
    private Long memberId;
    private TicketStatus status;

    public static TicketPayload toDto(TicketRequest request, String orderId) {
        return TicketPayload.builder()
                .orderId(orderId)
                .ticketTypeId(request.getTicketTypeId())
                .memberId(request.getMemberId())
                .status(TicketStatus.PENDING)
                .build();
    }
}
