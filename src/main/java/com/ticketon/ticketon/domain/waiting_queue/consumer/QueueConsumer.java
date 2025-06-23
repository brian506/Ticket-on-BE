package com.ticketon.ticketon.domain.waiting_queue.consumer;

import com.ticketon.ticketon.domain.waiting_queue.dto.WaitingMemberRequest;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        topics = "${kafka.topic-config.waiting.name}",
        groupId = "${kafka.consumer.waiting-group.group-id}",
        containerFactory = "waitingKafkaListenerContainerFactory"
)
public class QueueConsumer {

    private final ZSetOperations<String, String> zSetOps;

    public QueueConsumer(ZSetOperations<String, String> zSetOps) {
        this.zSetOps = zSetOps;
    }

    @KafkaHandler
    public void process(WaitingMemberRequest waitingUserDto, Acknowledgment ack) {
        // TODO: 예약 서버 여유 확인 및 처리 로직 작성
        zSetOps.remove("waiting-line", waitingUserDto.getUserId());
        ack.acknowledge();
    }
}