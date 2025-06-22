package com.ticketon.ticketon.domain.waiting_queue.producer;

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

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ZSetOperations<String, String> zSetOps;

    public QueueProducer(KafkaTemplate<String, String> kafkaTemplate, ZSetOperations<String, String> zSetOps) {
        this.kafkaTemplate = kafkaTemplate;
        this.zSetOps = zSetOps;
    }

    public void enqueue(final String userId) {
        // ZSet에 사용자 등록 (점수는 timestamp) 사용 todo 밀리세컨드까지 동일하게 들어온 사용자에 대한 예외도 고려해야 할듯
        double score = (double) Instant.now().toEpochMilli();

        zSetOps.add("waiting-line", userId, score);

        // 2) Kafka 전송
        kafkaTemplate.send("ticket-queue", userId);
    }


    public Long getMyQueuePosition(final String userId) {
        // 내 순위 조회
        Long myRank = zSetOps.rank("waiting-line", userId);
        if (myRank == null) {
            return -1L;
        }
        return myRank;
    }
}