package com.ticketon.ticketon.domain.payment.controller;

import com.ticketon.ticketon.domain.payment.dto.PaymentRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentResponse;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.service.PaymentService;
import com.ticketon.ticketon.utils.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<SuccessResponse> createPayment(@RequestBody PaymentRequest paymentRequest) {
            Payment payment = paymentService.confirmPayment(paymentRequest);
            SuccessResponse response = new SuccessResponse(true,"결제 승인 요청 성공", payment);
            return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
