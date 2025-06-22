package com.ticketon.ticketon.domain.waiting_queue.consumer;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {

    private final ZSetOperations<String, String> zSetOps;

    public QueueConsumer(ZSetOperations<String, String> zSetOps) {
        this.zSetOps = zSetOps;
    }

    @KafkaListener(topics = "ticket-queue", groupId = "ticket-group")
    public void process(String userId) {
        // TODO: 예약 서버에 여유 있는지 확인 후 처리

        // 처리 완료 시 ZSet에서 제거
        zSetOps.remove("waiting-line", userId);

    }
}