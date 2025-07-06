package com.ticketon.ticketon.controller;

import com.ticketon.ticketon.dto.SuccessResponse;
import com.ticketon.ticketon.producer.WaitingLineProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.*;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/v1/api/queues")
public class QueueController {

    private final WaitingLineProducer kafkaQueueProducer;

    public QueueController(WaitingLineProducer kafkaQueueProducer) {
        this.kafkaQueueProducer = kafkaQueueProducer;
    }

    @PostMapping("/enter")
    public Mono<ResponseEntity<SuccessResponse>> enter(@RequestParam String email) {
        return Mono.fromRunnable(() -> kafkaQueueProducer.sendQueueEnterMessage(email))
                .subscribeOn(Schedulers.parallel())
                .thenReturn(ResponseEntity.ok(new SuccessResponse(true, "대기열 등록 완료", null)));
    }
}
