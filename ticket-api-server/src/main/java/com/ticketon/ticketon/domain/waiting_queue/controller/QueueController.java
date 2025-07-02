package com.ticketon.ticketon.domain.waiting_queue.controller;

import com.ticketon.ticketon.domain.member.entity.CustomUserDetails;
import com.ticketon.ticketon.global.annotation.CurrentUser;
import com.ticketon.ticketon.utils.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/api/queues")
public class QueueController {

//    private final QueueProducer producer;
//
//    public QueueController(QueueProducer producer) {
//        this.producer = producer;
//    }

//    @PostMapping("/enter")
//    public Mono<ResponseEntity<SuccessResponse>> enter(@CurrentUser Mono<CustomUserDetails> userDetailsMono) {
//        return userDetailsMono.flatMap(userDetails -> {
//            String email = userDetails.getUsername();
//            return Mono.fromRunnable(() -> producer.enqueue(email))
//                    .thenReturn(ResponseEntity.ok(new SuccessResponse(true, "대기열 등록 완료", null)));
//        });
//    }

}