package com.ticketon.ticketon.domain.waiting_queue.dto;

public class WaitingMemberRequest {
    private String email;
    private long timestamp;

    public WaitingMemberRequest() {}

    public WaitingMemberRequest(String email, long timestamp) {
        this.email = email;
        this.timestamp = timestamp;
    }

    public String getEmail() {
        return email;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
