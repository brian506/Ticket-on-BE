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
import java.util.function.Supplier;

@Service(TicketIssueStrategyType.REDIS_STRATEGY_NAME)
@Qualifier(TicketIssueStrategyType.REDIS_STRATEGY_NAME)
@RequiredArgsConstructor
public class RedisLockTicketIssueService {

    private final RedissonClient redissonClient;
    private final TicketTypeRepository ticketTypeRepository;


    public <T> T redisLockOnMessage(String lockName, Supplier<T> action) {
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean available = lock.tryLock(1, 3, TimeUnit.SECONDS);
            if (!available) {
                throw new RuntimeException("락 획득 실패");
            }
            return action.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException();
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
