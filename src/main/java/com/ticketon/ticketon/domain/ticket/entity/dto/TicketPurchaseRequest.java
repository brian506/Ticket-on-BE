package com.ticketon.ticketon.domain.ticket.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketPurchaseRequest {

    private Long ticketTypeId;
    private Integer quantity;

}
