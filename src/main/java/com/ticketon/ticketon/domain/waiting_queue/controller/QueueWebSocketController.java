package com.ticketon.ticketon.domain.waiting_queue.controller;

import com.ticketon.ticketon.domain.waiting_queue.producer.QueueProducer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class QueueWebSocketController {

    private final QueueProducer queueProducer;

    public QueueWebSocketController(QueueProducer queueProducer) {
        this.queueProducer = queueProducer;
    }

    // 클라이언트가 보낸 userId로 순번 조회
    @MessageMapping("/queue-status")
    @SendTo("/topic/position")
    public Long getQueueStatus(String userId) {
        return queueProducer.getMyQueuePosition(userId);
    }
}