package com.ticketon.ticketon.consumer;

import com.ticketon.ticketon.dto.WaitingMemberRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@KafkaListener(
        topics = "${kafka.topic-config.waiting.name}",
        groupId = "${kafka.consumer.waiting-group.group-id}",
        containerFactory = "waitingKafkaListenerContainerFactory"
)
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
        String email = dto.getEmail();

        // Kafka 메시지 도착은 입장 허용 이벤트임 (순서 보장 완료된 상태)
        // Redis에 허용 상태 기록 (TTL 2분)
        redisTemplate.opsForValue().set("allowed:" + email, "true", Duration.ofMinutes(2));

        // 웹소켓으로 사용자에게 알림 전송
        messagingTemplate.convertAndSendToUser(email, "/topic/allowed", email);

        log.info("[입장 허용 알림 발송] email: {}", email);

        ack.acknowledge();
    }
}
