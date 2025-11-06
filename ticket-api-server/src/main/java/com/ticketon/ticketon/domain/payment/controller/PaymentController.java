package com.ticketon.ticketon.domain.payment.controller;

import com.ticket.dto.SuccessResponse;
import com.ticketon.ticketon.domain.payment.dto.*;
import com.ticketon.ticketon.domain.payment.service.PaymentGatewayService;
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

    private final PaymentGatewayService paymentGatewayService;
    private final PaymentService paymentService;



    // 2. PG 사 결제 요청 후 상태 PAID 로 변환 후 아웃박스 엔티티에 저장
    @PostMapping("/confirm")
    public ResponseEntity<?> createPayment(@RequestBody PaymentConfirmRequest paymentConfirmRequest) {
            PaymentMessage message = paymentGatewayService.confirmPayment(paymentConfirmRequest);
            log.info("payment 객체 요청 : {}",paymentConfirmRequest.getMemberId());
            SuccessResponse response = new SuccessResponse(true,"결제 승인 요청 성공", message);
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
