package com.ticketon.ticketon.domain.payment.dto;

public record OutboxEvent(PaymentMessage message) {
}
