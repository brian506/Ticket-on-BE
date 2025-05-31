package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketStatus;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;


    /**
     * todo 삭제 예정 (모니터링 테스트를 위한 임시 메서드)
     */
    public void testSave(){
        Ticket ticket = Ticket.builder()
                .eventName("testEvent")
                .seatInfo("A열13번")
                .price(10000)
                .eventDateTime(LocalDateTime.now())
                .status(TicketStatus.RESERVED)
                .build();
        ticketRepository.save(ticket);
    }

    public List<Ticket> testFindAll() {
        return ticketRepository.findAll();
    }
}
