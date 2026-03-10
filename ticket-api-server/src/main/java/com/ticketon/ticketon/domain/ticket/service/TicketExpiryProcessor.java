package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.ticket.dto.ExpiredTicket;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketExpiryProcessor {

    private final TicketRepository ticketRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String ISSUED_QUANTITY_KEY_PREFIX = "issued_quantity:";

    @Transactional
    public void processExpiredTickets(
            List<Long> cancelTicketIds,
            LocalDateTime now,
            List<ExpiredTicket> ticketsToRestore
    ) {
        ticketRepository.bulkUpdateStatusToExpiredByIds(cancelTicketIds, now);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                restoreRedisStock(ticketsToRestore);
            }
        });
    }

    private void restoreRedisStock(List<ExpiredTicket> ticketsToRestore) {
        for (ExpiredTicket ticket : ticketsToRestore) {
            try {
                redisTemplate.opsForValue()
                        .increment(ISSUED_QUANTITY_KEY_PREFIX + ticket.ticketTypeId(), 1);
                log.info("[Ticket] Redis 재고 복구 완료: ticketId={}, ticketTypeId={}",
                        ticket.ticketId(), ticket.ticketTypeId());
            } catch (Exception e) {
                log.error("[Ticket] Redis 재고 복구 실패 - 수동 확인 필요: ticketId={}, ticketTypeId={}",
                        ticket.ticketId(), ticket.ticketTypeId(), e);
            }
        }
    }
}
