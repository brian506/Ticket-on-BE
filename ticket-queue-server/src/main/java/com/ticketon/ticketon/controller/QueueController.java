package com.ticketon.ticketon.controller;

import com.ticket.dto.SuccessResponse;
import com.ticketon.ticketon.dto.EmailRequest;
import com.ticketon.ticketon.producer.WaitingLineProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/queues")
public class QueueController {

    private final WaitingLineProducer kafkaQueueProducer;

    public QueueController(WaitingLineProducer kafkaQueueProducer) {
        this.kafkaQueueProducer = kafkaQueueProducer;
    }

    @PostMapping("/enter")
    public Mono<ResponseEntity<SuccessResponse>> enter(@RequestBody EmailRequest request) {
        String email = request.email();
        kafkaQueueProducer.enqueue(email);
        return Mono.just(ResponseEntity.ok(new SuccessResponse(true, "요청 수신 완료", null)));
    }
}
