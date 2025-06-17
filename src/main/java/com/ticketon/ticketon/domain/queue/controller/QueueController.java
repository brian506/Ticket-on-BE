package com.ticketon.ticketon.domain.queue.controller;


import com.ticketon.ticketon.domain.member.entity.CustomUserDetails;
import com.ticketon.ticketon.domain.queue.service.QueueRedisService;
import com.ticketon.ticketon.global.annotation.CurrentUser;
import com.ticketon.ticketon.utils.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class QueueController {

    private final QueueRedisService queueRedisService;

    /**
     * 1. 예약 서버에 아무도 없어도 일단은 대기 큐에 넣고 순차적으로 순서를 지정해준다.
     * - 서버 부하 조절과 공정성을 보장하기 위해
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinQueue(@CurrentUser CustomUserDetails user) {
        queueRedisService.pushToQueue(user.getMemberId());
        SuccessResponse response = new SuccessResponse(true, "대기열 접속 성공", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
