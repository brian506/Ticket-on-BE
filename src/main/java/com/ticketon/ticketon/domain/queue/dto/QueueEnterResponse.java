package com.ticketon.ticketon.domain.queue.dto;

import lombok.Getter;

@Getter
public class QueueEnterResponse {
    private String ticketId;
    private Long position;
    public QueueEnterResponse(String ticketId, Long position) {
        this.ticketId = ticketId;
        this.position = position;
    }
}
