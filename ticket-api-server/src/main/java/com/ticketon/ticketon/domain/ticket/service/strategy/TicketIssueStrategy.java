package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;

public interface TicketIssueStrategy {
    TicketType purchaseTicket(PaymentMessage message, Long memberId);
}
