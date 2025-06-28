package com.ticketon.ticketon.domain.ticket.controller;

import com.ticketon.ticketon.domain.payment.dto.PaymentResponse;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import com.ticketon.ticketon.utils.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/{eventId}")
    public ResponseEntity<SuccessResponse> getTicketsByEventId(@PathVariable Long eventId) {
        //todo 반환 DTO 추가
        ticketService.findTicketsByEventId(eventId);
        SuccessResponse response = new SuccessResponse(true,"예약 가능한 티켓 조회",null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
