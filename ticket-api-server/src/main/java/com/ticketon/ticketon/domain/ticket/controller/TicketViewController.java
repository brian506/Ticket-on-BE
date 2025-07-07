package com.ticketon.ticketon.domain.ticket.controller;


import com.ticketon.ticketon.domain.eventitem.dto.EventItemResponse;
import com.ticketon.ticketon.domain.eventitem.service.EventItemService;
import com.ticketon.ticketon.domain.member.entity.CustomUserDetails;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketResponse;

import com.ticketon.ticketon.domain.ticket.service.TicketService;
import com.ticketon.ticketon.domain.ticket.service.strategy.TicketIssueStrategyType;
import com.ticketon.ticketon.global.annotation.CurrentUser;
import com.ticketon.ticketon.global.constants.Urls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TicketViewController {

    private final TicketService ticketService;
    private final EventItemService eventItemService;

//    // 티켓 구매
//    @PostMapping(Urls.TICKET_PURCHASE)
//    public String purchaseTicket(TicketPurchaseRequest ticketPurchaseRequest, @CurrentUser CustomUserDetails userDetails) throws AccessDeniedException {
//        validateTicketAccessFor(userDetails);
//
//        ticketService.purchaseTicket(TicketIssueStrategyType.OPTIMISTIC.getCode(), ticketPurchaseRequest, userDetails.getMember().getId());
//        return "payment/paymentConfirm";
//    }



    // 티켓 페이지
    @GetMapping(Urls.TICKET_RESERVATION)
    public String purchaseTicketPage(Model model, @RequestParam Long eventId, @CurrentUser CustomUserDetails userDetails) throws AccessDeniedException {
        validateTicketAccessFor(userDetails);

        EventItemResponse eventItem = eventItemService.getEventItemById(eventId);
        model.addAttribute("eventItem", eventItem);
        return "ticket/test/ticketPurchase";
    }

    // 대기열을 통과한 유저인지 검증
    private void validateTicketAccessFor(CustomUserDetails userDetails) throws AccessDeniedException {
        String memberEmail = userDetails.getUsername();
        //ticketAccessValidator.validTicketAccess(memberEmail);
    }

    // 내 티켓 조회 페이지
    @GetMapping(Urls.MY_TICKETS)
    public String myTicketsPage(@CurrentUser CustomUserDetails customUserDetails, Model model) {
        List<TicketResponse> tickets = ticketService.findMyTickets(customUserDetails.getMember().getId());
        model.addAttribute("tickets", tickets);
        return "/ticket/test/myTickets";
    }

    // 내 티켓 취소
    @PostMapping(Urls.TICKET_CANCEL)
    public String myTicketCancel(@RequestParam Long ticketId, @CurrentUser CustomUserDetails customUserDetails, Model model) {
        ticketService.cancelMyTicket(customUserDetails.getMember().getId(), ticketId);
        model.addAttribute("ticketId", ticketId);
        return "redirect:/my-tickets";
    }
}
