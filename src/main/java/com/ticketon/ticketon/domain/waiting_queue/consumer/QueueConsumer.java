package com.ticketon.ticketon.domain.waiting_queue.consumer;

import com.ticketon.ticketon.domain.waiting_queue.dto.WaitingMemberRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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
public class QueueConsumer {

    private final ZSetOperations<String, String> zSetOps;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    public QueueConsumer(ZSetOperations<String, String> zSetOps, SimpMessagingTemplate messagingTemplate, RedisTemplate<String, String> redisTemplate) {
        this.zSetOps = zSetOps;
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
    }

    @KafkaHandler
    public void process(WaitingMemberRequest waitingUserDto, Acknowledgment ack) {
        String userId = waitingUserDto.getUserId();
        // TODO: 예약 서버 여유 확인 및 처리 로직 작성
        zSetOps.remove("waiting-line", userId);
        redisTemplate.opsForValue().set("allowed:" + userId, "true", Duration.ofMinutes(2));
        messagingTemplate.convertAndSendToUser(userId, "/topic/allowed", userId);
        ack.acknowledge();
    }
}