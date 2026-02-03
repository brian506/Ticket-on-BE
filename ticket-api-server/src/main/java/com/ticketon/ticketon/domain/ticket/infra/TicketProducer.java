package com.ticketon.ticketon.domain.ticket.infra;

import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketProducer {

    @Value("${kafka.topic-config.ticket.name}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendNewTicket(TicketRequest ticketRequest, String orderId) {
        kafkaTemplate.send(topic,ticketRequest.toEvent(orderId));
    }
}
