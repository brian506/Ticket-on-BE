package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.utils.OptionalUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service(TicketIssueStrategyType.REDIS_STRATEGY_NAME)
@Qualifier(TicketIssueStrategyType.REDIS_STRATEGY_NAME)
@RequiredArgsConstructor
public class RedisLockTicketIssueService implements TicketIssueStrategy{

    private final RedissonClient redissonClient;
    private final TicketTypeRepository ticketTypeRepository;

    @Transactional
    @Override
    public TicketType purchaseTicket(PaymentMessage message, Long memberId) {
        String lockName = "ticketTypeLock" + message.getTicketTypeId();
        RLock lock = redissonClient.getLock(lockName);
        Long ticketTypeId = message.getTicketTypeId();

        try {
            boolean available = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!available) {
                throw new RuntimeException("락 획득 실패");
            }
            TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findByIdForUpdate(ticketTypeId), "티켓 타입 조회 실패 ticket_id=" + ticketTypeId);
            return ticketType;

        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
