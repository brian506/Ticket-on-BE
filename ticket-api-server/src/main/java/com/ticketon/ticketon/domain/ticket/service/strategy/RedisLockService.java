package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticketon.ticketon.domain.ticket.dto.TicketPayload;
import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.infra.TicketProducer;
import com.ticketon.ticketon.domain.ticket.repository.TicketRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLockService {

    private final TicketRedisRepository redisRepository;
    private final TicketIssueStrategy ticketIssueStrategy;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> decreaseStockScript;
    private final TicketProducer ticketProducer;
    private final List<Long> soldOutTicketIds = new ArrayList<>();
    private final static String KEY_PREFIX = "issued_quantity:";

    public TicketReadyResponse purchaseTicket(TicketRequest request, String orderId) {
        String lockKey = "LOCK:TICKET:" + request.getTicketTypeId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean available = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!available) {
                log.warn("[redis warn] : 락 획득 실패");
                throw new IllegalStateException("[redis warn] : 접속량이 많아 실패했습니다.");
            }
            return ticketIssueStrategy.purchaseTicketRedis(request.getTicketTypeId(), request.getMemberId(), orderId);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("[Redis error] : Redisson Lock already unlocked");
            }
        }
    }


    public boolean purchaseTicketLua(TicketRequest request, String orderId) {
        Long ticketTypeId = request.getTicketTypeId();
        if(soldOutTicketIds.contains(ticketTypeId)) {
            return false;
        }

        String stockKey = KEY_PREFIX + ticketTypeId;
        Long result = redisTemplate.execute(decreaseStockScript, List.of(stockKey), "1");

        if (result == -1L) {
            throw new IllegalStateException("시스템 오류: 재고 정보가 없습니다.");
        }
        if (result == 0L) {
            soldOutTicketIds.add(ticketTypeId);
            return false;
        }
        redisRepository.savePendingTicket(TicketPayload.toDto(request, orderId));

        try {
            ticketProducer.sendNewTicket(request, orderId);

        } catch (Exception e) {
            redisTemplate.opsForValue().increment(stockKey, 1); // 재고 복구
            redisRepository.delete(orderId);
            throw e;
        }
        return true;
    }

}
