package com.ticketon.ticketon.domain.ticket.controller;

import com.ticketon.ticketon.domain.ticket.service.TicketService;
import com.ticketon.ticketon.exception.custom.NotFoundDataException;
import com.ticketon.ticketon.utils.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/tickets")
    public ResponseEntity<?> testapi(){
        throw new NotFoundDataException("존재하지 않는 데이터 입니다.");
        //SuccessResponse<?> response = new SuccessResponse<>(true,"test", null);
        //return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
