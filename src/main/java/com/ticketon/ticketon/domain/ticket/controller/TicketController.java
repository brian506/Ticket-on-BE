package com.ticketon.ticketon.domain.ticket.controller;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequestDto;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import com.ticketon.ticketon.exception.custom.NotFoundDataException;
import com.ticketon.ticketon.utils.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/tickets")
    public ResponseEntity<?> testapi(){
        throw new NotFoundDataException("존재하지 않는 데이터 입니다.");
        //SuccessResponse<?> response = new SuccessResponse<>(true,"test", null);
        //return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // 티켓 구매
    @PostMapping("/ticket")
    public String purchaseTicket(TicketPurchaseRequestDto ticketPurchaseRequestDto, @AuthenticationPrincipal Member member) { //AuthenticationPrincipal 나중에 CustomUserDetails 만들어서 리팩토링 해줘야 함.
        ticketService.purchaseTicket(ticketPurchaseRequestDto, member.getId());
        return "redirect:/success";
    }


    @GetMapping("/purchase/success")
    public String purchaseTicket() {
        return "purchaseComplete";
    }

}
