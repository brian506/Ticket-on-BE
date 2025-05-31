package com.ticketon.ticketon.domain.wating_queue.controller;

import com.ticketon.ticketon.domain.wating_queue.dto.QueueEnterResponse;
import com.ticketon.ticketon.domain.wating_queue.dto.QueueStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

import java.util.UUID;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private static final String QUEUE_KEY = "ticket_queue";

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 대기얼 진입
    @PostMapping("/enter")
    public ResponseEntity<?> enterQueue() {
        String ticketId = UUID.randomUUID().toString();
        // Redis List 뒤에 추가
        redisTemplate.opsForList().rightPush(QUEUE_KEY, ticketId);
        // TTL을 설정할 수 있음 (예: 1시간 후 자동 삭제)
        redisTemplate.expire(QUEUE_KEY, Duration.ofHours(1));

        // 내 현재 위치 조회 (인덱스)
        Long pos = redisTemplate.opsForList().indexOf(QUEUE_KEY, ticketId);
        if (pos == null) pos = -1L;

        return ResponseEntity.ok(new QueueEnterResponse(ticketId, pos));
    }

    // 내 위치 조회
    @GetMapping("/status/{ticketId}")
    public ResponseEntity<?> queueStatus(@PathVariable String ticketId) {
        // LPOS는 Redis 6+ 부터. Spring Data Redis → indexOf 로 동작
        Long pos = redisTemplate.opsForList().indexOf(QUEUE_KEY, ticketId);
        if (pos == null) {
            // 대기열에 없는 경우(이미 처리되었거나 잘못된 ID)
            return ResponseEntity.ok(new QueueStatusResponse(ticketId, -1L, false));
        }
        boolean isTurn = (pos == 0);
        return ResponseEntity.ok(new QueueStatusResponse(ticketId, pos, isTurn));
    }

    /** 3) 대기열에서 나가기(예: Timeout 또는 취소 시) */
    @DeleteMapping("/leave/{ticketId}")
    public ResponseEntity<Void> leaveQueue(@PathVariable String ticketId) {
        // 대기열에서 해당 ID 제거 (0개만 제거)
        redisTemplate.opsForList().remove(QUEUE_KEY, 0, ticketId);
        return ResponseEntity.noContent().build();
    }
}
