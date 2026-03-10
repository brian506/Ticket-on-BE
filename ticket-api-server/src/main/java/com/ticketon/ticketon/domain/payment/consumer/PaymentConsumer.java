package com.ticketon.ticketon.domain.payment.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Profile("!test")
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConsumer {

    private final TicketService ticketService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topic-config.payment.name}",
            groupId = "${kafka.consumer.payment-group.group-id}",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void consumePayment(List<String> messages, Acknowledgment ack) {
        try {
            List<PaymentMessage> paymentMessages = new ArrayList<>();
            for (String jsonMessage : messages) {
                JsonNode rootNode = objectMapper.readTree(jsonMessage);
                JsonNode payload = rootNode.get("payload");
                paymentMessages.add(objectMapper.treeToValue(payload, PaymentMessage.class));
            }
            ticketService.issueTicket(paymentMessages);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("[PaymentConsumer] 티켓 최종 발급 처리 실패: {}", messages);
            throw new RuntimeException("티켓 발급 실패 처리로 인한 재시도");
        }
    }
}
