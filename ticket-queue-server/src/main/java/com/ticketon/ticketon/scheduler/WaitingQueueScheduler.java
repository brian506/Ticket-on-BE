package com.ticketon.ticketon.scheduler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import static com.ticketon.ticketon.utils.RedisKeyConstants.ALLOWED_PREFIX;
import static com.ticketon.ticketon.utils.RedisKeyConstants.WAITING_LINE;
import static com.ticketon.ticketon.utils.RedisUtils.stripQuotesAndTrim;
import static com.ticketon.ticketon.utils.StompConstants.TOPIC_ALLOWED;


@Component
public class WaitingQueueScheduler {

    private final RedisTemplate<String, String> waitingRedisTemplate;
    private final RedisTemplate<String, String> allowedRedisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public WaitingQueueScheduler(
            @Qualifier("waitingRedisTemplate") RedisTemplate<String, String> waitingRedisTemplate,
            @Qualifier("reservationRedisTemplate") RedisTemplate<String, String> allowedRedisTemplate,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.waitingRedisTemplate = waitingRedisTemplate;
        this.allowedRedisTemplate = allowedRedisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    public void checkAndNotify() {
        Set<String> emails = waitingRedisTemplate.opsForZSet().range(WAITING_LINE, 0, 0);
        if (emails == null || emails.isEmpty()) {
            return;
        }
        String rawEmail = emails.iterator().next();
        String email = stripQuotesAndTrim(rawEmail);
        Long removedCount = waitingRedisTemplate.opsForZSet().remove(WAITING_LINE, email);
        if (removedCount != null && removedCount > 0) {
            allowedRedisTemplate.opsForValue().set(ALLOWED_PREFIX + email, "true", Duration.ofMinutes(2));
            messagingTemplate.convertAndSendToUser(email, TOPIC_ALLOWED, email);
        }
    }
}