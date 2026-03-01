package com.ticketon.ticketon.domain.ticket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketScheduler {

    private final TicketService ticketService;

//    @Scheduled(fixedRate = 60000)
    public void scheduledRemovePendingTickets() {
        ticketService.removePendingTickets();
    }
}
