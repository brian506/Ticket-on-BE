package com.ticketon.ticketon.domain.ticket.infra;

import com.ticketon.ticketon.domain.ticket.dto.NewTicketEvent;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketConsumer {

    private final TicketService ticketService;

    @KafkaListener(
            topics = "${kafka.topic-config.ticket.name}",
            groupId = "${kafka.consumer.ticket-group.group-id}",
            containerFactory = "ticketKafkaListenerContainerFactory")
    public void consumeNewTicket(List<NewTicketEvent> ticketEvents, Acknowledgment ack) {
        try {
            ticketService.issueTicketBatch(ticketEvents);
        } catch (Exception e) {
            log.warn("[TicketConsumer] 티켓 배치 생성 실패: {}", e.getMessage());
        }
        ack.acknowledge();
        log.info("[TicketConsumer] Ticket Batch 처리 완료: {}건", ticketEvents.size());
    }
}
