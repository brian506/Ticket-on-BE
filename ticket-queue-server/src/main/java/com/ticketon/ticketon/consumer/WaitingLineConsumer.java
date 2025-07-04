package com.ticketon.ticketon.consumer;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.ticketon.ticketon.utils.RedisKeyConstants.WAITING_LINE;
import static com.ticketon.ticketon.utils.RedisUtils.stripQuotesAndTrim;

@Component
public class WaitingLineConsumer {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public WaitingLineConsumer(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(
            topics = "${kafka.topic-config.queue-enqueue.name}",
            groupId = "${kafka.consumer.queue-enqueue.group-id}",
            containerFactory = "waitingEnqueueKafkaListenerContainerFactory"
    )
    public void listen(final String message) {
        String email = stripQuotesAndTrim(message);
        redisTemplate.opsForZSet()
                .add(WAITING_LINE, email, System.currentTimeMillis())
                .subscribe();
    }
}