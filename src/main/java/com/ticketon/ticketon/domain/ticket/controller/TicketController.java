package com.ticketon.ticketon.domain.ticket.controller;

import com.ticketon.ticketon.domain.member.entity.CustomUserDetails;
import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequestDto;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketResponseDto;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import com.ticketon.ticketon.exception.custom.NotFoundDataException;
import com.ticketon.ticketon.global.annotation.CurrentUser;
import com.ticketon.ticketon.global.constants.Urls;
import com.ticketon.ticketon.utils.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;


    // 티켓 구매
    @PostMapping(Urls.TICKET_PURCHASE)
    public String purchaseTicket(TicketPurchaseRequestDto ticketPurchaseRequestDto, @CurrentUser CustomUserDetails customUserDetails) {
        ticketService.purchaseTicket(ticketPurchaseRequestDto, customUserDetails.getMember().getId());
        return "redirect:/success";
    }

    // 내 티켓 조회 페이지
    // @로그인 된 유저만 접근가능
    @GetMapping(Urls.MY_TICKETS)
    public String myTicketsPage(@CurrentUser CustomUserDetails customUserDetails, Model model) {
        List<TicketResponseDto> tickets = ticketService.findMyTickets(customUserDetails.getMember().getId());
        model.addAttribute("tickets", tickets);
        return "/ticket/test/myTickets";
    }

    // 내 티켓 취소
    // @로그인 된 유저만 접근가능
    @PostMapping(Urls.TICKET_CANCEL)
    public String myTicketCancel(@RequestParam Long ticketId, @CurrentUser CustomUserDetails customUserDetails, Model model) {
        ticketService.cancelMyTicket(customUserDetails.getMember().getId(), ticketId);
        model.addAttribute("ticketId", ticketId);
        return "redirect:/my-tickets";
    }


    @GetMapping(Urls.TICKETS)
    public String purchaseTicket() {
        return "purchaseComplete";
    }

}
