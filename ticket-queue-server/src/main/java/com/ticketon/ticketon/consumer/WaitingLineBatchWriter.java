package com.ticketon.ticketon.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.ticketon.ticketon.utils.RedisKeyConstants.WAITING_LINE;

@Slf4j
@Component
public class WaitingLineBatchWriter {

    private final RedisTemplate<String, String> redisTemplate;
    private final BlockingQueue<String> buffer = new LinkedBlockingQueue<>(10000); // 최대 크기 제한

    public WaitingLineBatchWriter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void enqueue(String email) {
        boolean success = buffer.offer(email);
        if (!success) {
            log.warn("Buffer is full. Dropping message: {}", email);
        }
    }

    @Scheduled(fixedDelay = 200)
    public void flushToRedis() {
        int batchSize = 1000;
        Set<ZSetOperations.TypedTuple<String>> batch = new HashSet<>();

        for (int i = 0; i < batchSize; i++) {
            String email = buffer.poll();
            if (email == null) break;
            batch.add(new DefaultTypedTuple<>(email, (double) System.currentTimeMillis()));
        }

        if (!batch.isEmpty()) {
            redisTemplate.opsForZSet().add(WAITING_LINE, batch);
            log.debug("Flushed {} items to Redis", batch.size());
        }
    }
}