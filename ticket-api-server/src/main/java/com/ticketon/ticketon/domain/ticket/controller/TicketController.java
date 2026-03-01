package com.ticketon.ticketon.domain.ticket.controller;

import com.ticket.dto.SuccessResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseTicket(@RequestBody TicketRequest request) {
        String orderId = new ULID().nextULID();
        ticketService.purchaseTicket(request, orderId);
        SuccessResponse response = new SuccessResponse<>(true, "티켓 요청 성공", Map.of("orderId", orderId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/expire")
    public String triggerExpiry() {
        ticketService.removePendingTickets();
        return "만료 티켓 재고 복구 실행 완료";
    }
}
