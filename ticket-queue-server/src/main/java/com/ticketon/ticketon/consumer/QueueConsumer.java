package com.ticketon.ticketon.consumer;

import com.ticketon.ticketon.dto.WaitingMemberRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@KafkaListener(
        topics = "${kafka.topic-config.waiting.name}",
        groupId = "${kafka.consumer.waiting-group.group-id}",
        containerFactory = "waitingKafkaListenerContainerFactory"
)
@Slf4j
public class QueueConsumer {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public QueueConsumer(RedisTemplate<String, String> redisTemplate,
                         SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaHandler
    public void process(WaitingMemberRequest dto, Acknowledgment ack) {
        String email = dto.getEmail().replace("\"", "").trim();
        redisTemplate.opsForValue().set("allowed:" + email, "true", Duration.ofMinutes(2));
        messagingTemplate.convertAndSendToUser(email, "/topic/allowed", email);
        ack.acknowledge();
    }
}
