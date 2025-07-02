package com.ticketon.ticketon.domain.payment.producer;

import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmResponse;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {

    @Value("${kafka.topic-config.payment.name}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPayment(final PaymentConfirmResponse response) {
        PaymentMessage message = response.fromResponse(response);
        kafkaTemplate.send(topic, String.valueOf(message.getTicketTypeId()), message);
        // ticketId 를 기준으로 같은 파티션으로 전달(중복 방지)
    }
}
