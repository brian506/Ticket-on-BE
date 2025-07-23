package com.ticketon.ticketon.domain.payment.comsumer;

import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.payment.service.PaymentService;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;


@Profile("!test")
@Slf4j
@Service
@RequiredArgsConstructor
@KafkaListener(
        topics = "${kafka.topic-config.payment.name}",
        groupId = "${kafka.consumer.payment-group.group-id}",
        containerFactory = "paymentKafkaListenerContainerFactory")
public class PaymentConsumer {

    private final PaymentService paymentService;

    // 예약,결제 정보 저장
    @KafkaHandler // (6)
    public void consumePayment(PaymentMessage message, Acknowledgment ack) {
        try {
            log.info("message 정보 : {}",message.toString());
            if (message.getMemberId() == null || message.getTicketTypeId() == null) {
                log.warn("메시지 필드가 널임: {}", message);
                return; // Kafka ack 안 해주면 retry 발생
            }

           paymentService.saveTicketAndPayment(message);
            // 오프셋 ack 여부 판단
            ack.acknowledge();

        }catch (Exception e){
            log.error("payment message invalid : {}", e);
        }

    }
}

