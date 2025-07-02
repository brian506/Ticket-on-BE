package com.ticketon.ticketon.domain.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class TicketAccessValidator {

    private final RedisTemplate<String, String> redisTemplate;

    public void validTicketAccess(String userId) throws AccessDeniedException {
        String key = "allowed:" + userId;
        Boolean allowed = redisTemplate.hasKey(key);

        if (allowed == null || !allowed) {
            throw new AccessDeniedException("대기열을 통과하지 않은 사용자입니다.");
        }
    }
}
