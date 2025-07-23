package com.ticketon.ticketon.domain.ticket.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class TicketPurchaseRequest {

    private Long ticketTypeId;

    @Max(1)
    private Integer quantity;

}
