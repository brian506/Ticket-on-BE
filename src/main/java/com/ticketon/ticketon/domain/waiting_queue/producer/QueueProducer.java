package com.ticketon.ticketon.domain.waiting_queue.producer;

import com.ticketon.ticketon.domain.waiting_queue.dto.WaitingMemberRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * TODO
 * 여기서 고려해야 하는게, Redis TTL 이나 eviction 정책을
 * 대기열 순서라는 데이터 속성을 고려해 설정해야 Redis 메모리가 안정적으로 운영될 수 있을듯
 */
@Service
public class QueueProducer {

    @Value("${kafka.topic-config.waiting.name}")
    private String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ZSetOperations<String, String> zSetOps;
    private final RedisTemplate<String, String> redisTemplate;

    public QueueProducer(KafkaTemplate<String, Object> kafkaTemplate, ZSetOperations<String, String> zSetOps, RedisTemplate<String, String> redisTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.zSetOps = zSetOps;
        this.redisTemplate = redisTemplate;
    }

    public void enqueue(final String email) {
        // ZSet에 사용자 등록 (점수는 timestamp) 사용 todo 밀리세컨드까지 동일하게 들어온 사용자에 대한 예외도 고려해야 할듯
        double score = (double) Instant.now().toEpochMilli();
        zSetOps.add("waiting-line", email, score);
        WaitingMemberRequest dto = new WaitingMemberRequest(email, Instant.now().toEpochMilli());
        kafkaTemplate.send(topic, dto);
    }


    public Long getMyQueuePosition(final String email) {
        Long myRank = zSetOps.rank("waiting-line", email);
        if (myRank == null) {
            return -1L;
        }
        return myRank;
    }
}