package com.ticketon.ticketon.domain.ticket.controller;

import com.ticketon.ticketon.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/tickets")
public class TicketController {

    private final TicketService ticketService;


}
