package com.ticketon.ticketon.domain.payment.comsumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.exception.custom.KafkaConsumerException;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;


@Profile("!test")
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConsumer {

    private final TicketService ticketService;
    private final ObjectMapper objectMapper;


    // 예약,결제 정보 저장
    @KafkaListener(
            topics = "${kafka.topic-config.ticket.name}",
            groupId = "${kafka.consumer.payment-group.group-id}",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void consumePayment(String message, Acknowledgment ack) {
        try {
            // Debezium 이 보낸 전체 JSON 문자열을 JsonNode 로 파싱
            JsonNode rootNode = objectMapper.readTree(message);
            // payload 에 있는 실제 메시지(PaymentMessage) 추출
            JsonNode payload = rootNode.get("payload");
            PaymentMessage payment = objectMapper.treeToValue(payload, PaymentMessage.class);
            ticketService.issueTicket(payment);
            ack.acknowledge(); // 작업 성공시 브로커에게 완료 메시지 전송
        } catch (Exception e) {
            log.error("[kafka 처리] 티켓 최종 발급 처리 실패. payload {}", message);
            throw new RuntimeException("티켓 발급 실패 처리로 인한 재시도");
        }

    }


}

