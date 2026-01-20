package com.ticketon.ticketon.domain.ticket.dto;

import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import jakarta.validation.constraints.Max;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {

    private Long ticketTypeId;
    @Max(1)
    private Integer quantity;
    private Long memberId;
    private Integer amount;

    public static TicketRequest from(Long memberId, TicketType ticketType) {
        return TicketRequest.builder()
                .ticketTypeId(ticketType.getId())
                .memberId(memberId)
                .amount(Math.toIntExact(ticketType.getPrice()))
                .build();
    }
}
