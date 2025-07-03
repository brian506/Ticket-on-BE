package com.ticketon.ticketon.controller;

import com.ticketon.ticketon.producer.KafkaQueueProducer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class QueueWebSocketController {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public QueueWebSocketController(ReactiveRedisTemplate<String, String> redisTemplate, SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/queue-status")
    public void getQueueStatus(String email, SimpMessageHeaderAccessor accessor) {
        String sessionEmail = accessor.getUser().getName();
        if (!sessionEmail.equals(email)) return;
        redisTemplate.opsForZSet().rank("waiting-line", email)
                .defaultIfEmpty(-1L)
                .subscribe(position -> {
                    messagingTemplate.convertAndSendToUser(email, "/queue/position", position);
                });
    }
}
