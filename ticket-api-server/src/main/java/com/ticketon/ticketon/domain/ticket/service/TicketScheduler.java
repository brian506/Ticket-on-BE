package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.ticket.dto.ExpiredTicket;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.ListUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TicketScheduler {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;


    @Scheduled(cron = "0 0 0 0 1 *")
    @Transactional
    public void removePendingTickets() {
        List<ExpiredTicket> tickets = ticketRepository.findExpiredTickets(LocalDateTime.now());
        ticketTypeRepository.decreaseIssuedTickets(tickets.getFirst().ticketTypeId(), ticketRepository.count());

        ticketRepository.bulkUpdateStatusToExpired(LocalDateTime.now());
    }
}
