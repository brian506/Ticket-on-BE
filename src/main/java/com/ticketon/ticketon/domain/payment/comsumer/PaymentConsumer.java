package com.ticketon.ticketon.domain.payment.comsumer;

import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.payment.service.PaymentService;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
@KafkaListener(
        topics = "${kafka.topic-config.payment.name}",
        groupId = "${kafka.consumer.payment-group.group-id}",
        containerFactory = "paymentKafkaListenerContainerFactory")
public class PaymentConsumer {

    private final PaymentRepository paymentRepository;

    // 예약,결제 정보 저장
    @KafkaHandler
    public void consumePayment(PaymentMessage message, Acknowledgment ack) {
        try {
            // 메시지 수신
            log.info("received payment message: {}", message);
            // 메시지 유효성 검사
            if(message == null) {
                throw new IllegalArgumentException("payment message is null");
            }
            // 예약 정보 저장
            Payment payment = message.toEntity(message);
            paymentRepository.save(payment);
            // 오프셋 ack 여부 판단
            ack.acknowledge();

        }catch (Exception e){
            log.error("payment message invalid : {}", e.getMessage());
        }

    }
}

