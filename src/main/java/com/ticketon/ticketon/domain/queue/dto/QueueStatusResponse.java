package com.ticketon.ticketon.domain.queue.dto;

import lombok.Getter;

@Getter
public class QueueStatusResponse {
    public String ticketId;
    public Long position;
    public boolean isTurn; // position == 0 이면 true
    public QueueStatusResponse(String ticketId, Long position, boolean isTurn) {
        this.ticketId = ticketId;
        this.position = position;
        this.isTurn = isTurn;
    }
}
