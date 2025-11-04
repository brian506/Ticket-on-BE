package com.ticketon.ticketon.domain.payment.comsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.exception.custom.KafkaConsumerException;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.payment.service.PaymentService;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import com.ticketon.ticketon.domain.ticket.service.strategy.RedisLockTicketIssueService;
import com.ticketon.ticketon.utils.OptionalUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;


@Profile("!test")
@Slf4j
@Service
@RequiredArgsConstructor
@KafkaListener(
        topics = "${kafka.topic-config.payment.name}",
        groupId = "${kafka.consumer.payment-group.group-id}",
        containerFactory = "paymentKafkaListenerContainerFactory")
public class PaymentConsumer {

    private final TicketService ticketService;
    private final ObjectMapper objectMapper;


    // 예약,결제 정보 저장
    @KafkaHandler
    public void consumePayment(String payload, Acknowledgment ack) {
        try{
            PaymentMessage message = objectMapper.readValue(payload, PaymentMessage.class);
            ticketService.issueTicket(message);
            ack.acknowledge(); // 작업 성공시 브로커에게 완료 메시지 전송
        }
        catch (JsonProcessingException e) { // json 변환실패
            // 이 메시지는 영원히 처리할 수 없으므로, ack를 보내고 로그를 남깁니다.
            log.error("JSON 파싱 실패. payload: {}", payload);
            ack.acknowledge();
        }
        catch (KafkaConsumerException e){
            log.error("티켓 최종 발급 처리 실패. payload {}",payload);
        }

    }



}

