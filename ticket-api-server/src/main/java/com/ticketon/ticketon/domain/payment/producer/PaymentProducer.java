package com.ticketon.ticketon.domain.payment.producer;

import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmResponse;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProducer {

    @Value("${kafka.topic-config.payment.name}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPayment(final PaymentConfirmResponse response, final PaymentConfirmRequest request) {
        PaymentMessage message = response.fromResponse(response,request);
        log.info("paymentMessage : {} " , message);
        kafkaTemplate.send(topic, message.getOrderId(), message);
        // orderId 를 기준으로 같은 파티션으로 전달(중복 방지)
    }
}
