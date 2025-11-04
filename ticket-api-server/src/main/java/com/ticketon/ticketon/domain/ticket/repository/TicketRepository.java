package com.ticketon.ticketon.domain.ticket.repository;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByMember_Id(Long memberId);

    // PENDING -> PAID
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Ticket t SET t.ticketStatus = 'PAID' " +
            "WHERE t.id = :ticketId AND t.ticketStatus = 'PENDING' AND t.expiredAt > CURRENT_TIMESTAMP")
    int updateTicketStatus(@Param("ticketId") Long ticketId);
}
