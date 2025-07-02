package com.ticketon.ticketon.producer;

import com.ticketon.ticketon.dto.WaitingMemberRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class KafkaQueueProducer {

    @Value("${kafka.topic-config.waiting.name}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ZSetOperations<String, String> zSetOps;

    public KafkaQueueProducer(KafkaTemplate<String, Object> kafkaTemplate,
                              ZSetOperations<String, String> zSetOps) {
        this.kafkaTemplate = kafkaTemplate;
        this.zSetOps = zSetOps;
    }

    public void enqueue(final String email) {
        long timestamp = Instant.now().toEpochMilli();
        zSetOps.add("waiting-line", email, timestamp);
        // 대기열에 넣기만 하고 입장 허용은 Kafka 알림으로 처리
    }

    public Long getMyQueuePosition(final String email) {
        Long rank = zSetOps.rank("waiting-line", email);
        return rank != null ? rank : -1L;
    }
}
