package com.ticketon.ticketon.domain.payment.controller;

import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import com.ticketon.ticketon.domain.eventitem.service.EventItemService;
import com.ticketon.ticketon.domain.member.entity.CustomUserDetails;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.payment.service.PaymentService;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketResponse;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import com.ticketon.ticketon.global.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentViewController {


    private final TicketService ticketService;
    private final EventItemService eventItemService;
    private final PaymentService paymentService;

    @Value("${toss.client-key}")
    private String clientKey;

    // 결제 요청 시 필요한 예약 정보
    @GetMapping
    public String paymentRequest(TicketPurchaseRequest request,@CurrentUser CustomUserDetails userDetails, Model model){
        TicketRequest ticketRequest = ticketService.purchaseTicket(request, userDetails.getMemberId());
        String eventTitle = eventItemService.getTitleByTicketTypeId(request.getTicketTypeId());
        model.addAttribute("clientKey",clientKey);
        model.addAttribute("ticketTypeId",ticketRequest.getTicketTypeId());
        model.addAttribute("memberId",ticketRequest.getMemberId());
        model.addAttribute("amount",ticketRequest.getAmount());
        model.addAttribute("orderName",eventTitle);
        return "paymentConfirm";
    }
    // 결제 성공
    @GetMapping("/success")
    public String confirmPayment(@RequestParam String paymentKey,
                                 @RequestParam Long orderId,
                                 @RequestParam Long memberId,
                                 @RequestParam int amount,
                                 PaymentConfirmRequest request,
                                 Model model){
        paymentService.confirmPayment(request);
        model.addAttribute("paymentKey",paymentKey);
        model.addAttribute("ticketTypeId",orderId);
        model.addAttribute("memberId",memberId);
        model.addAttribute("amount",amount);
        return "paymentSuccess";
    }

    // 결제 실패
    @GetMapping("/fail")
    public String failPayment(@RequestParam(required = false) String code,
                              @RequestParam(required = false) String message,
                              Model model){
        model.addAttribute("code",code);
        model.addAttribute("message",message);
        return "paymentFailure";
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
        return "paymentCancel";    // templates/cancel.html
    }

}
