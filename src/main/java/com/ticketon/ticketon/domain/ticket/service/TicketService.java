package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public void save(){

    }
}
