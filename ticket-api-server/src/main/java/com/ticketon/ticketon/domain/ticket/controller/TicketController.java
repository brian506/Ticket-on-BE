package com.ticketon.ticketon.domain.ticket.controller;

import com.ticket.dto.SuccessResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import com.ticketon.ticketon.domain.ticket.service.strategy.RedisLockService;
import com.ticketon.ticketon.global.constants.Urls;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ticket")
public class TicketController {

    private final TicketService ticketService;
    private final RedisLockService redisLockService;

//    @PostMapping("/ticket-request")
//    public ResponseEntity<?> requestTicket(@RequestBody TicketRequest request){
//        String orderId = new ULID().nextULID();
//        TicketReadyResponse readyResponse = redisLockService.purchaseTicket(request);
//        SuccessResponse response = new SuccessResponse<>(true,"티켓 결제 요청 성공",readyResponse);
//        return new ResponseEntity<>(response,HttpStatus.OK);
//    }

    @PostMapping("/ticket-request")
    public ResponseEntity<?> requestTicket(@RequestBody TicketRequest request) {
        String orderId = new ULID().nextULID();
        SuccessResponse response;
        if(!redisLockService.purchaseTicketLua(request, orderId)){
             response = new SuccessResponse<>(false,"티켓 재고 부족",null);
        } else {
            Map<String, String> data = new HashMap<>();
            data.put("orderId", orderId);
            response = new SuccessResponse<>(true,"티켓 요청 성공",data);
        }

        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
