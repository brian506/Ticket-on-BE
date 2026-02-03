package com.ticketon.ticketon.domain.ticket.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketon.ticketon.domain.ticket.dto.TicketPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class TicketRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "ticket_request:";
    private static final long TTL_MINUTES = 10L;

    public void save(TicketPayload ticketPayload) {
        String key = KEY_PREFIX + ticketPayload.getOrderId();
        try {
            String value = objectMapper.writeValueAsString(ticketPayload);
            redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(TTL_MINUTES));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 저장 오류");
        }
    }

    public TicketPayload get(String orderId) {
        String key = KEY_PREFIX + orderId;
        String value = (String) redisTemplate.opsForValue().get(key);
        if(value == null) return null;

        try{
            return objectMapper.readValue(value, TicketPayload.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 조회 오류");
        }

    }

    public void delete(String orderId) {
        redisTemplate.delete(KEY_PREFIX + orderId);
    }
}
