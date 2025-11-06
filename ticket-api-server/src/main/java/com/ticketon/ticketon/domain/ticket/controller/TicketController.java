package com.ticketon.ticketon.domain.ticket.controller;

import com.ticket.dto.SuccessResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import com.ticketon.ticketon.global.constants.Urls;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ticket")
public class TicketController {

    private final TicketService ticketService;

    // 1. 먼저 결제 요청
    @PostMapping("/ticket-request")
    public ResponseEntity<?> requestTicket(@RequestBody TicketRequest request){
        TicketReadyResponse readyResponse = ticketService.purchaseTicket(request);
        SuccessResponse response = new SuccessResponse<>(true,"티켓 결제 요청 성공",readyResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
