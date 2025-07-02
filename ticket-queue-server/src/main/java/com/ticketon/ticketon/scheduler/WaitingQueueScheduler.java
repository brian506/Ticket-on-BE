package com.ticketon.ticketon.scheduler;

import com.ticketon.ticketon.dto.WaitingMemberRequest;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
public class WaitingQueueScheduler {

    private final ZSetOperations<String, String> zSetOps;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public WaitingQueueScheduler(ZSetOperations<String, String> zSetOps,
                                 KafkaTemplate<String, Object> kafkaTemplate,
                                 @org.springframework.beans.factory.annotation.Value("${kafka.topic-config.waiting.name}") String topic) {
        this.zSetOps = zSetOps;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Scheduled(fixedDelay = 1000)
    public void checkAndNotify() {
        Set<String> topUserSet = zSetOps.range("waiting-line", 0, 0);
        if (topUserSet == null || topUserSet.isEmpty()) {
            return;
        }
        String email = topUserSet.iterator().next();
        Long removed = zSetOps.remove("waiting-line", email);
        if (removed != null && removed > 0) {
            kafkaTemplate.send(topic, new WaitingMemberRequest(email, Instant.now().toEpochMilli()));
        }
    }

}