package com.ticketon.ticketon.payment;

import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmResponse;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.producer.PaymentProducer;
import com.ticketon.ticketon.domain.payment.service.PaymentGateway;
import com.ticketon.ticketon.domain.payment.service.PaymentService;
import com.ticketon.ticketon.domain.payment.service.TossPaymentGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.TestPropertySource;

import java.time.OffsetDateTime;

@ExtendWith(MockitoExtension.class)
public class PaymentUnitTest {

    // mock 기반 외부 호출 api 테스트
    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private PaymentProducer paymentProducer;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("결제 승인 요청 성공")
    void confirmPayment_Success(){
        //given
        PaymentConfirmRequest request = PaymentConfirmRequest.builder()
                .paymentKey("test-payment-key")
                .orderId("asdfasdf")
                .amount(1000)
                .build();

        OffsetDateTime requestedAt = OffsetDateTime.parse("2025-07-12T14:30:00+09:00");
        OffsetDateTime approvedAt = OffsetDateTime.parse("2025-07-12T14:30:05+09:00");

        PaymentConfirmResponse response = PaymentConfirmResponse.builder()
                .orderId("asdfasdf")
                .amount(15000)
                .paymentKey("test-payment-key")
                .requestedAt(requestedAt)
                .approvedAt(approvedAt)
                .build();

        // when
        Mockito.when(paymentGateway.requestPaymentConfirm(request))
                .thenReturn(response);

        // then
        paymentService.confirmPayment(request);
        Mockito.verify(paymentGateway).requestPaymentConfirm(request);
        Mockito.verify(paymentProducer).sendPayment(response,request);
    }

}
