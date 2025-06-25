package com.ticketon.ticketon.domain.payment.comsumer;

import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
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

    private final PaymentService paymentService;
    private final TicketService ticketService;

    @KafkaHandler
    public void consumePayment(PaymentConfirmRequest paymentRequest, Acknowledgment ack) {
        try {
            // 메시지 수신
            log.info("received payment message: {}", paymentRequest);
            // 메시지 유효성 검사
            if (paymentRequest == null) {
                log.warn("payment message is null");
                throw new IllegalArgumentException("payment message is null");
            }
            // redis 락으로 중복 방지
            // confirmPayment 호출
            paymentService.confirmPayment(paymentRequest);
            // 예약 저장
            //ticketService.purchaseTicket();
            // 오프셋 a    ck 여부 판단
            ack.acknowledge();

        } catch (Exception e) {
            log.error("payment message invalid : {}", e.getMessage());
        }
    }
}
