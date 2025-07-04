package com.ticketon.ticketon.controller;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import static com.ticketon.ticketon.utils.RedisKeyConstants.DEFAULT_POSITION;
import static com.ticketon.ticketon.utils.RedisKeyConstants.WAITING_LINE;
import static com.ticketon.ticketon.utils.StompConstants.QUEUE_POSITION_DESTINATION;

@Controller
public class QueueWebSocketController {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public QueueWebSocketController(ReactiveRedisTemplate<String, String> redisTemplate, SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 현재 자신의 상태 조회
     */
    @MessageMapping("/queue-status")
    public void getQueueStatus(String email, SimpMessageHeaderAccessor accessor) {
        String sessionEmail = accessor.getUser().getName();
        if (!sessionEmail.equals(email)) return;
        redisTemplate.opsForZSet().rank(WAITING_LINE, email)
                .defaultIfEmpty(DEFAULT_POSITION)
                .subscribe(position -> {
                    messagingTemplate.convertAndSendToUser(email, QUEUE_POSITION_DESTINATION, position);
                });
    }
}
