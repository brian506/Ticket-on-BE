package com.ticketon.ticketon.domain.ticket.repository.custom;

import com.ticketon.ticketon.domain.ticket.dto.ExpiredTicket;

import java.time.LocalDateTime;
import java.util.List;

public interface TicketCustomRepository {
    Long updateTicketStatus(Long ticketId);
    List<ExpiredTicket> findExpiredTickets(LocalDateTime now);
    void bulkUpdateStatusToExpired(LocalDateTime now);
}
