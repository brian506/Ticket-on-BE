package com.ticketon.ticketon.domain.ticket.infra;

import com.ticketon.ticketon.domain.ticket.dto.NewTicketEvent;
import com.ticketon.ticketon.domain.ticket.service.strategy.TicketIssueStrategy;
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

    private final TicketIssueStrategy ticketIssueStrategy;

    @KafkaListener(
            topics = "${kafka.topic-config.ticket.name}",
            groupId = "${kafka.consumer.ticket-group.group-id}",
            containerFactory = "ticketKafkaListenerContainerFactory")
    public void consumeNewTicket(List<NewTicketEvent> ticketEvents, Acknowledgment ack) {

        try {
            ticketIssueStrategy.requestTicketKafka(ticketEvents);
        } catch (Exception e) {
            log.warn("[Ticket Batch] 티켓 생성 실패");
        }


        ack.acknowledge();
        log.info("🎫 Ticket Batch 처리 완료 ({}건)", ticketEvents.size());
    }

}
