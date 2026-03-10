package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticket.exception.custom.ExceededTicketQuantityException;
import com.ticketon.ticketon.domain.ticket.dto.TicketPayload;
import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.TicketStatus;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.infra.TicketProducer;
import com.ticketon.ticketon.domain.ticket.repository.TicketRedisRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.domain.ticket.service.TicketIssueStrategy;
import com.ticketon.ticketon.utils.OptionalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class RedisLuaTicketIssueStrategy implements TicketIssueStrategy {

    private final TicketRedisRepository redisRepository;
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> decreaseStockScript;
    private final TicketProducer ticketProducer;
    private final TicketTypeRepository ticketTypeRepository;

    private static final String STOCK_KEY_PREFIX = "issued_quantity:";

    private final List<Long> soldOutTicketIds = new ArrayList<>();

    @Override
    public TicketReadyResponse issue(TicketRequest request, String orderId) {
        Long ticketTypeId = request.getTicketTypeId();

        if (soldOutTicketIds.contains(ticketTypeId)) {
            TicketType ticketType = OptionalUtil.getOrElseThrow(
                    ticketTypeRepository.findById(ticketTypeId), "존재하지 않는 티켓입니다.");
            throw new ExceededTicketQuantityException(ticketType.getName(), ticketType.getPrice());
        }

        String stockKey = STOCK_KEY_PREFIX + ticketTypeId;
        Long result = redisTemplate.execute(decreaseStockScript, List.of(stockKey), "1");

        if (result == null || result == -1L) {
            throw new IllegalStateException("시스템 오류: 재고 정보가 없습니다.");
        }

        if (result == 0L) {
            soldOutTicketIds.add(ticketTypeId);
            TicketType ticketType = OptionalUtil.getOrElseThrow(
                    ticketTypeRepository.findById(ticketTypeId), "존재하지 않는 티켓입니다.");
            throw new ExceededTicketQuantityException(ticketType.getName(), ticketType.getPrice());
        }

        redisRepository.savePendingTicket(TicketPayload.toDto(request, orderId));

        try {
            ticketProducer.sendNewTicket(request, orderId);
        } catch (Exception e) {
            log.error("[RedisLuaStrategy] Kafka 발행 실패, 재고 복구 및 Redis 삭제: orderId={}", orderId);
            redisTemplate.opsForValue().increment(stockKey, 1);
            redisRepository.delete(orderId);
            throw e;
        }

        return TicketReadyResponse.builder()
                .ticketTypeId(request.getTicketTypeId())
                .memberId(request.getMemberId())
                .ticketStatus(TicketStatus.PENDING)
                .price(request.getAmount())
                .orderId(orderId)
                .build();
    }
}
