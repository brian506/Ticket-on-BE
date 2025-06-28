package com.ticketon.ticketon.domain.waiting_queue.controller;

import com.ticketon.ticketon.domain.waiting_queue.producer.QueueProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class QueueWebSocketController {

    private final QueueProducer queueProducer;
    private final SimpMessagingTemplate messagingTemplate;

    public QueueWebSocketController(QueueProducer queueProducer, SimpMessagingTemplate messagingTemplate) {
        this.queueProducer = queueProducer;
        this.messagingTemplate = messagingTemplate;
    }

    // 클라이언트가 보낸 userId로 순번 조회
    @MessageMapping("/queue-status")
    @SendTo("/queue/position")
    public void getQueueStatus(String email, SimpMessageHeaderAccessor accessor) {
        Long position = queueProducer.getMyQueuePosition(email);
        messagingTemplate.convertAndSendToUser(email, "/queue/position", position);
    }

}