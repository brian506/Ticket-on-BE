package com.ticketon.ticketon.domain.eventitem.entity;

public enum EventItemStatus {
    HIDDEN,   // 관리자만 볼 수 있는 비공개 상태
    OPEN,     // 유저들에게 공개된 상태 (티켓 판매 가능)
    CLOSED,   // 판매 마감 상태 (공연 진행 예정이거나 종료)
    CANCELED  // 공연 취소 상태
}
