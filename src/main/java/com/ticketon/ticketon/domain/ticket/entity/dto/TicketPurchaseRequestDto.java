package com.ticketon.ticketon.domain.ticket.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketPurchaseRequestDto {

    private Long ticketTypeId;
    private Integer quantity;

}
