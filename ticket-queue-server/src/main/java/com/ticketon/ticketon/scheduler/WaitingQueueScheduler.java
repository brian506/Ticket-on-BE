package com.ticketon.ticketon.scheduler;

import com.ticketon.ticketon.dto.WaitingMemberRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;


@Component
public class WaitingQueueScheduler {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic-config.waiting.name}")
    private String topic;

    public WaitingQueueScheduler(ReactiveRedisTemplate<String, String> redisTemplate,
                                 KafkaTemplate<String, Object> kafkaTemplate) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    public void checkAndNotify() {
        //todo
        redisTemplate.opsForZSet()
                .range("waiting-line", Range.closed(0L, 0L))
                .collectList()
                .flatMap(emails -> {
                    if (emails.isEmpty()) return Mono.empty();
                    String email = emails.get(0);
                    return redisTemplate.opsForZSet().remove("waiting-line", email)
                            .filter(r -> r > 0)
                            .doOnNext(r -> {
                                kafkaTemplate.send(topic, new WaitingMemberRequest(email, Instant.now().toEpochMilli()));
                            })
                            .then();
                })
                .subscribe();
    }
}