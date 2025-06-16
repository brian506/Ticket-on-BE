package com.ticketon.ticketon.domain.queue.controller;


import com.ticketon.ticketon.domain.queue.dto.BookingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book")
public class BookingController {

    private static final String QUEUE_KEY = "ticket_queue";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping
    public BookingResponse bookTicket(@RequestParam String ticketId) {
        // 1) 현재 대기열의 맨 앞이 나인지 확인
        String headId = redisTemplate.opsForList().index(QUEUE_KEY, 0);
        if (!ticketId.equals(headId)) {
            return new BookingResponse(false, "아직 순서가 아닙니다.");
        }

        // 2) 실제 예약 로직: 좌석 할당 등 처리 (여기선 샘플로 성공만 응답)
        // ... (DB 트랜잭션, 좌석 배정 등) ...

        // 3) 대기열에서 제거 (LPOP 으로 맨 앞 제거)
        redisTemplate.opsForList().leftPop(QUEUE_KEY);

        return new BookingResponse(true, "예약이 완료되었습니다.");
    }
}
