package com.ticketon.ticketon.domain.payment.dto;

public record TossConfirmRequest(String paymentKey, String orderId, int amount) {
}
