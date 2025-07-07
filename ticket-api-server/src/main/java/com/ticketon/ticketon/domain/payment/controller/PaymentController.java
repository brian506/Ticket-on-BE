package com.ticketon.ticketon.domain.payment.controller;

import com.ticketon.ticketon.domain.member.entity.CustomUserDetails;
import com.ticketon.ticketon.domain.payment.dto.PaymentCancelRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentResponse;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.service.PaymentService;
import com.ticketon.ticketon.global.annotation.CurrentUser;
import com.ticketon.ticketon.utils.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<SuccessResponse> createPayment(@RequestBody PaymentConfirmRequest paymentConfirmRequest) {
            paymentService.confirmPayment(paymentConfirmRequest);
            log.info("payment 객체 요청 : {}",paymentConfirmRequest.getMemberId());
            SuccessResponse response = new SuccessResponse(true,"결제 승인 요청 성공", paymentConfirmRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public ResponseEntity<SuccessResponse> cancelPayment(@RequestBody PaymentCancelRequest paymentCancelRequest) {
        paymentService.cancelPayment(paymentCancelRequest);
        SuccessResponse response = new SuccessResponse(true,"결제 취소 요청 성공", paymentCancelRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/info/{ticketTypeId}")
    public ResponseEntity<SuccessResponse> getPaymentByTicketId(@PathVariable Long ticketTypeId) {
        PaymentResponse paymentResponse = paymentService.findByTicketTypeId(ticketTypeId);
        SuccessResponse response = new SuccessResponse(true,"결제 조회 성공",paymentResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
