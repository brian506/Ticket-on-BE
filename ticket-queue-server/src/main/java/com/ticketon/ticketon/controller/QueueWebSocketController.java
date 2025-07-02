package com.ticketon.ticketon.controller;

import com.ticketon.ticketon.producer.KafkaQueueProducer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class QueueWebSocketController {

    private final KafkaQueueProducer queueProducer;
    private final SimpMessagingTemplate messagingTemplate;

    public QueueWebSocketController(KafkaQueueProducer queueProducer,
                                    SimpMessagingTemplate messagingTemplate) {
        this.queueProducer = queueProducer;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/queue-status")
    public void getQueueStatus(String email, SimpMessageHeaderAccessor accessor) {
        // 요청자 인증 검증
        String sessionUserEmail = accessor.getUser().getName();
        if (!sessionUserEmail.equals(email)) {
            // todo 인증 실패 처리
            return;
        }
        Long position = queueProducer.getMyQueuePosition(email);
        messagingTemplate.convertAndSendToUser(email, "/queue/position", position);
    }
}
