package com.ticketon.ticketon.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class KafkaQueueProducer {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Value("${kafka.topic-config.waiting.name}")
    private String topic;

    public KafkaQueueProducer(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> enqueue(String email) {
        long timestamp = Instant.now().toEpochMilli();
        return redisTemplate.opsForZSet().add("waiting-line", email, timestamp);
    }

    public Mono<Long> getMyQueuePosition(String email) {
        return redisTemplate.opsForZSet().rank("waiting-line", email)
                .defaultIfEmpty(-1L);
    }
}