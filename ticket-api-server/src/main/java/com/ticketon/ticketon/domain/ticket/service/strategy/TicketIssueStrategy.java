package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;

public interface TicketIssueStrategy {
    TicketRequest purchaseTicket(TicketPurchaseRequest request, Long memberId);
}
