package com.ticketon.ticketon.domain.ticket.repository;

import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.repository.custom.TicketCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> , TicketCustomRepository {
    List<Ticket> findByMember_Id(Long memberId);

    Optional<Ticket> findByOrderId(String orderId);
    List<Ticket> findAllByOrderIdIn(List<String> orderIds);

    boolean existsByOrderId(String orderId);
}
