package com.ticketon.ticketon.domain.ticket.controller;

import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import com.ticketon.ticketon.exception.custom.NotFoundDataException;
import com.ticketon.ticketon.utils.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/tickets")
    public ResponseEntity<?> testFind() {
        List<Ticket> tickets = ticketService.testFindAll();
        SuccessResponse<?> response = new SuccessResponse<>(true, "티켓 모두 조회 성공", tickets);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/tickets")
    public ResponseEntity<?> testSave() {
        ticketService.testSave();
        SuccessResponse<?> response = new SuccessResponse<>(true, "티켓 저장 성공", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
