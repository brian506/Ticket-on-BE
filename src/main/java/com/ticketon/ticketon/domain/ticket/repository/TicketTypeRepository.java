package com.ticketon.ticketon.domain.ticket.repository;

import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
}
