package com.ticketon.ticketon.domain.ticket.service.strategy;

public enum TicketIssueStrategyType {
    OPTIMISTIC("optimistic"),
    PESSIMISTIC("pessimistic"),
    REDIS("redis");

    public static final String OPTIMISTIC_STRATEGY_NAME = "optimistic";
    public static final String PESSIMISTIC_STRATEGY_NAME = "pessimistic";
    public static final String REDIS_STRATEGY_NAME = "redis";

    private final String code;

    TicketIssueStrategyType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}