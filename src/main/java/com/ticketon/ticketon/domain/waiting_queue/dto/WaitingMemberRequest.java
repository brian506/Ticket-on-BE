package com.ticketon.ticketon.domain.waiting_queue.dto;

public class WaitingMemberRequest {
    private String userId;
    private long timestamp;

    public WaitingMemberRequest() {}

    public WaitingMemberRequest(String userId, long timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
