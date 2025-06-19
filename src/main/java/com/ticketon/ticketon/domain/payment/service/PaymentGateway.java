package com.ticketon.ticketon.domain.payment.service;

import com.ticketon.ticketon.domain.payment.dto.PaymentCancelRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentCancelResponse;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmResponse;

public interface PaymentGateway {
    //todo PaymentConfirmResponse 와 PaymentConfirmRequest 에 대한 추상클래스 만들면 좋을듯함
    PaymentConfirmResponse requestPaymentConfirm(PaymentConfirmRequest request);
    PaymentCancelResponse requestPaymentCancel(PaymentCancelRequest request);
}