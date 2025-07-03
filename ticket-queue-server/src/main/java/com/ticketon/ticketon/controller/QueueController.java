package com.ticketon.ticketon.controller;

import com.ticketon.ticketon.dto.SuccessResponse;
import com.ticketon.ticketon.producer.KafkaQueueProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.*;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/v1/api/queues")
public class QueueController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic-config.queue-enqueue.name}")
    private String topic;

    public QueueController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/enter")
    public Mono<ResponseEntity<SuccessResponse>> enter(@RequestParam String email) {
        return Mono.fromCallable(() -> {
            kafkaTemplate.send(topic, email);
            return ResponseEntity.ok(new SuccessResponse(true, "대기열 등록 완료", null));
        }).subscribeOn(Schedulers.boundedElastic()); // 블로킹 작업을 별도 스레드풀에서 실행
    }
}
