package com.ticketon.ticketon.domain.queue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueRedisService {

    @Qualifier("queueRedisTemplate")
    private final RedisTemplate<String, Object> queueRedisTemplate;

    public void pushToQueue(final String userId) {
        queueRedisTemplate.opsForList().rightPush("waiting_queue", userId);
    }
}