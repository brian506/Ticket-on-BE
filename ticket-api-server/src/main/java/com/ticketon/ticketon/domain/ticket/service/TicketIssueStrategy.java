package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;

public interface TicketIssueStrategy {

    TicketReadyResponse issue(TicketRequest request, String orderId);
}
