package com.ticketon.ticketon.controller;

import com.ticketon.ticketon.dto.SuccessResponse;
import com.ticketon.ticketon.producer.KafkaQueueProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/queues")
public class QueueController {

    private final KafkaQueueProducer producer;

    public QueueController(KafkaQueueProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/enter")
    public Mono<ResponseEntity<SuccessResponse>> enter(@RequestParam final String email) {
        System.out.println("enqueu = " + email);
        return Mono.fromRunnable(() -> producer.enqueue(email))
                .thenReturn(
                        ResponseEntity.ok(new SuccessResponse(true, "대기열 등록 완료", null))
                );
    }
}
