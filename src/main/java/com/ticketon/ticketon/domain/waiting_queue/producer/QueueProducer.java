package com.ticketon.ticketon.domain.waiting_queue.producer;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    public QueueProducer(KafkaTemplate<String, String> kafkaTemplate, RedisTemplate<String, String> redisTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.redisTemplate = redisTemplate;
    }

    public void enqueue(String userId) {
        // Redis에 대기열 등록
        redisTemplate.opsForList().rightPush("waiting-line", userId);

        // Kafka로 전송
        kafkaTemplate.send("ticket-queue", userId);
    }

    public Long getWaitingNumber(String userId) {
        List<String> queue = redisTemplate.opsForList().range("waiting-line", 0, -1);
        if (queue == null) return -1L;
        return queue.indexOf(userId) + 1L;
    }
}