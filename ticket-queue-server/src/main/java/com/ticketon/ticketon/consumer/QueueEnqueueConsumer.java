package com.ticketon.ticketon.consumer;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Redis 저장을 순차적으로 실행하는 컨슈머
 */
@Component
public class QueueEnqueueConsumer {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public QueueEnqueueConsumer(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(
            topics = "${kafka.topic-config.queue-enqueue.name}",
            groupId = "${kafka.consumer.queue-enqueue.group-id}",
            containerFactory = "waitingEnqueueKafkaListenerContainerFactory"
    )
    public void listen(String message) {
        String email = message.replace("\"", "").trim();
        redisTemplate.opsForZSet()
                .add("waiting-line", email, System.currentTimeMillis())
                .subscribe();
    }
}