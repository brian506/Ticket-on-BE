package com.ticketon.ticketon.domain.payment.controller;

import com.ticketon.ticketon.domain.eventitem.service.EventItemService;
import com.ticketon.ticketon.domain.member.entity.CustomUserDetails;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.payment.service.PaymentGateway;
import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;

import com.ticketon.ticketon.domain.ticket.service.TicketService;

import com.ticketon.ticketon.global.annotation.CurrentUser;
import com.ticketon.ticketon.global.constants.Urls;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping(Urls.PAYMENT)
@RequiredArgsConstructor
public class PaymentViewController {

    private final TicketService ticketService;
    private final EventItemService eventItemService;
    private final PaymentGateway paymentGateway;

    @Value("${toss.client-key}")
    private String clientKey;

    // 결제 요청 시 필요한 예약 정보
    @GetMapping(Urls.PAYMENT_PREPARE)
    public String paymentRequest(@RequestParam Long ticketTypeId,
                                 @RequestParam @Valid int quantity,
                                 @CurrentUser CustomUserDetails userDetails,Model model){
        TicketPurchaseRequest ticketPurchaseRequest = new TicketPurchaseRequest(ticketTypeId,quantity);
        TicketRequest ticketRequest = ticketService.requestTicket(ticketPurchaseRequest, userDetails.getMemberId());
        TicketReadyResponse response = ticketService.purchaseTicket(ticketRequest);
        String eventTitle = eventItemService.getTitleByTicketTypeId(ticketPurchaseRequest.getTicketTypeId());
        model.addAttribute("clientKey", clientKey);
        model.addAttribute("orderId", response.getOrderId());
        model.addAttribute("ticketTypeId", ticketTypeId);
        model.addAttribute("memberId", response.getMemberId());
        model.addAttribute("amount", response.getPrice());
        model.addAttribute("orderName", eventTitle);
        return "payment/paymentConfirm";
    }

    // 결제 성공
    @GetMapping("/success")
    public String confirmPayment(@RequestParam String paymentKey,
                                 @RequestParam String orderId,
                                 @RequestParam int amount,
                                 Model model){
        model.addAttribute("paymentKey",paymentKey);
        model.addAttribute("orderId",orderId);
        model.addAttribute("amount",amount);
        return "payment/paymentSuccess";
    }

    // 결제 실패
    @GetMapping("/fail")
    public String failPayment(@RequestParam(required = false) String code,
                              @RequestParam(required = false) String message,
                              Model model){
        model.addAttribute("code",code);
        model.addAttribute("message",message);
        return "payment/paymentFailure";
    }

    // 결제 취소
    @GetMapping("/cancel")
    public String cancelPage(
            @RequestParam String paymentKey,
            @RequestParam Integer amount,
            Model model
    ) {
        model.addAttribute("paymentKey",   paymentKey);
        model.addAttribute("cancelAmount", amount);
        return "payment/paymentCancel";
    }

}
