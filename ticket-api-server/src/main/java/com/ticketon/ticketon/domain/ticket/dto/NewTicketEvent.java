package com.ticketon.ticketon.domain.ticket.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewTicketEvent {
    private Long ticketTypeId;
    private Long memberId;
    private String orderId;
    private Integer price;

}
