package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;

public interface TicketIssueStrategy {
    void purchaseTicket(TicketPurchaseRequest request, Long memberId);
}
