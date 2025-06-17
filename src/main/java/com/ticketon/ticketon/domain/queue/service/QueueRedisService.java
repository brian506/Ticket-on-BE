package com.ticketon.ticketon.domain.queue.service;

import com.ticketon.ticketon.domain.queue.handler.QueueWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueRedisService {

    private static final String WAITING_QUEUE_PREFIX = "waiting_queue";
    private final QueueWebSocketHandler queueWebSocketHandler;

    @Qualifier("queueRedisTemplate")
    private final RedisTemplate<String, Object> queueRedisTemplate;

    public void pushToQueue(final long memberId) {
        queueRedisTemplate.opsForList().rightPush(WAITING_QUEUE_PREFIX, memberId);
    }

    /**
     * 한명의 사용자가 빠졌을때
     */
    public void popAndNotify() {
        Long memberId = popFromQueue();
        if (memberId != null) {
            //웹소켓에 최신화된 대기 인원을 알려줌
            Long remaining = queueRedisTemplate.opsForList().size(WAITING_QUEUE_PREFIX);
            queueWebSocketHandler.sendMessageToAll("현재 대기인원: " + remaining);
        }
    }

    public Long popFromQueue() {
        Object memberId = queueRedisTemplate.opsForList().leftPop(WAITING_QUEUE_PREFIX);
        return memberId == null ? null : (Long) memberId;
    }

    public int getUserPosition(long memberId) {
        List<Object> queue = queueRedisTemplate.opsForList().range(WAITING_QUEUE_PREFIX, 0, -1);
        return queue == null ? -1 : queue.indexOf(memberId) + 1;  // 1-based index
    }
}