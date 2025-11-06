package com.ticketon.ticketon.domain.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketon.ticketon.domain.payment.dto.OutboxEvent;
import com.ticketon.ticketon.domain.payment.entity.OutboxMessage;
import com.ticketon.ticketon.domain.payment.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OutboxEventService {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    // savePayment() 트랜잭션 이후에 Outbox INSERT 수행
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void savePaymentToOutbox(OutboxEvent event){
        try {
            String jsonPayload = objectMapper.writeValueAsString(event.message());
            OutboxMessage message = OutboxMessage.toEntityFromTicket(jsonPayload);
            outboxRepository.save(message);
        } catch (JsonProcessingException e) {
            // 직렬화 실패는 심각한 시스템 오류이므로 RuntimeException 처리
            throw new RuntimeException("Kafka payload 직렬화에 실패했습니다.", e);
        }
    }
}
