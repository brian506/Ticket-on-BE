package com.ticketon.ticketon.domain.payment.service;

import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmResponse;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class PaymentGatewayService {

    private final PaymentService paymentService;

    //todo pg 사 호출 - 성공,취소 예외 처리
    public PaymentMessage confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
        // 부하테스트용 ( pg 호출 x )
        String orderId = new ULID().nextULID();
        PaymentConfirmResponse response = new PaymentConfirmResponse(orderId,10000,"test-key", OffsetDateTime.now(),OffsetDateTime.now());
//        PaymentConfirmResponse response = paymentGateway.requestPaymentConfirm(paymentConfirmRequest);
        PaymentMessage message =  response.fromResponse(response,paymentConfirmRequest);
        paymentService.savePayment(paymentConfirmRequest,message);
        return message;
    }
}
