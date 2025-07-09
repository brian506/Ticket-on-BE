package com.ticketon.ticketon.domain.payment.service;

import com.ticketon.ticketon.domain.payment.dto.PaymentCancelRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentCancelResponse;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmResponse;

public interface PaymentGateway {
    PaymentConfirmResponse requestPaymentConfirm(PaymentConfirmRequest request);
    PaymentCancelResponse requestPaymentCancel(PaymentCancelRequest request);
}