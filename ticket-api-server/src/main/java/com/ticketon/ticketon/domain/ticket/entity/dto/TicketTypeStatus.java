package com.ticketon.ticketon.domain.ticket.entity.dto;

public enum TicketTypeStatus {
    READY,      // 판매 전
    ON_SALE,    // 판매 중
    PAUSED,     // 일시중단
    SOLD_OUT,   // 매진
    ENDED       // 판매 종료
}
