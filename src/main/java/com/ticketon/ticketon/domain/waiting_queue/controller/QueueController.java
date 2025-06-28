package com.ticketon.ticketon.domain.waiting_queue.controller;

import com.ticketon.ticketon.domain.member.entity.CustomUserDetails;
import com.ticketon.ticketon.domain.waiting_queue.producer.QueueProducer;
import com.ticketon.ticketon.global.annotation.CurrentUser;
import com.ticketon.ticketon.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        producer.enqueue(email);
        SuccessResponse response = new SuccessResponse(true, "대기열 등록 완료", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 테스트를 위한 임시 REST API 대기열 순서 확인은 이후 WebSocket 으로 변경
     */
    @GetMapping("/position")
    public ResponseEntity<?> getPosition(@CurrentUser CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        Long pos = producer.getMyQueuePosition(email);
        SuccessResponse response = new SuccessResponse(true, "내 앞에 몇명이 있는지?", pos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}