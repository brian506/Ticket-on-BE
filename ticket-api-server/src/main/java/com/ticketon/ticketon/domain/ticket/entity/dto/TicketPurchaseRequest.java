package com.ticketon.ticketon.domain.ticket.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class TicketPurchaseRequest {

    private Long ticketTypeId;
    private Integer quantity;

}
