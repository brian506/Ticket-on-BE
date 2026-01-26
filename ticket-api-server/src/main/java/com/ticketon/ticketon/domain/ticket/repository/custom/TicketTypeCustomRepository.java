package com.ticketon.ticketon.domain.ticket.repository.custom;

import com.ticketon.ticketon.domain.ticket.entity.TicketType;

import java.util.List;
import java.util.Optional;

public interface TicketTypeCustomRepository {
    Optional<TicketType> findByIdForUpdate(Long ticketTypeId);
    Long increaseTicketAtomically(Long ticketTypeId);
    void decreaseIssuedTickets(Long ticketTypeId, Long count);
}
