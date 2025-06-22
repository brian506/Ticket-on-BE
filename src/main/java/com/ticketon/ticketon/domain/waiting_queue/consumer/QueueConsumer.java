package com.ticketon.ticketon.domain.waiting_queue.consumer;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {

    private final RedisTemplate<String, String> redisTemplate;

    public QueueConsumer(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(topics = "ticket-queue", groupId = "ticket-group")
    public void process(String userId) {
        //todo 예약 서버에 몇명의 사용자가 있고, 더 들어갈 수 있는지를 판단하는 로직이 있어야함

        // 처리 완료 시 Redis에서 제거
        redisTemplate.opsForList().remove("waiting-line", 1, userId);

        // 처리 상태 저장
        redisTemplate.opsForValue().set("status:" + userId, "COMPLETED");
    }
}