package com.ticketon.ticketon.domain.waiting_queue.controller;

import com.ticketon.ticketon.domain.member.entity.CustomUserDetails;
import com.ticketon.ticketon.domain.waiting_queue.producer.QueueProducer;
import com.ticketon.ticketon.global.annotation.CurrentUser;
import com.ticketon.ticketon.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/queues")
public class QueueController {

    private final QueueProducer producer;

    public QueueController(QueueProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/enter")
    public ResponseEntity<?> enter(@CurrentUser CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        System.out.println("email == " + email);
        enqueueAsync(email);
        SuccessResponse response = new SuccessResponse(true, "대기열 등록 완료", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Async("taskExecutor")
    public void enqueueAsync(String email) {
        producer.enqueue(email);
    }
}