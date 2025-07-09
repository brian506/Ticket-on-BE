package com.ticketon.ticketon.payment;

import com.ticketon.ticketon.domain.payment.service.TossPaymentGateway;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.TestPropertySource;

@RestClientTest(value = TossPaymentGateway.class)
@TestPropertySource("classpath:application.yml")
public class PaymentUnitTest {


}
