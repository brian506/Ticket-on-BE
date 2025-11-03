package com.ticketon.ticketon.domain.payment.controller;

import com.ticket.dto.SuccessResponse;
import com.ticketon.ticketon.domain.payment.dto.PaymentCancelRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmResponse;
import com.ticketon.ticketon.domain.payment.dto.PaymentResponse;
import com.ticketon.ticketon.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/payments")
public class PaymentController {

    private final PaymentService paymentService;


    // 2. PG 사 결제 요청
    @PostMapping("/confirm")
    public ResponseEntity<SuccessResponse> createPayment(@RequestBody PaymentConfirmRequest paymentConfirmRequest) {
            PaymentConfirmResponse confirmResponse = paymentService.confirmPayment(paymentConfirmRequest);
            log.info("payment 객체 요청 : {}",paymentConfirmRequest.getMemberId());
            SuccessResponse response = new SuccessResponse(true,"결제 승인 요청 성공", confirmResponse);
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
