package com.ticketon.ticketon.domain.waiting_queue.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import static com.ticketon.ticketon.utils.RedisKeyConstants.DEFAULT_POSITION;
import static com.ticketon.ticketon.utils.RedisKeyConstants.WAITING_LINE;
import static com.ticketon.ticketon.utils.StompConstants.QUEUE_POSITION_DESTINATION;


@Controller
public class QueueWebSocketController {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public QueueWebSocketController(@Qualifier("waitingRedisTemplate") RedisTemplate<String, String> redisTemplate, SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/queues/status")
    public void getQueueStatus(String email, SimpMessageHeaderAccessor accessor) {
        if (!isAuthorizedUser(email, accessor)) {
            return;
        }
        Long position = getQueuePosition(email);
        sendQueuePositionToUser(email, position);
    }

    private boolean isAuthorizedUser(String email, SimpMessageHeaderAccessor accessor) {
        String sessionEmail = accessor.getUser() != null ? accessor.getUser().getName() : null;
        return email != null && email.equals(sessionEmail);
    }

    private Long getQueuePosition(String email) {
        Long position = redisTemplate.opsForZSet().rank(WAITING_LINE, email);
        if (position == null) {
            return DEFAULT_POSITION;
        }
        return position;
    }

    private void sendQueuePositionToUser(String email, Long position) {
        messagingTemplate.convertAndSendToUser(email, QUEUE_POSITION_DESTINATION, position);
    }
}
