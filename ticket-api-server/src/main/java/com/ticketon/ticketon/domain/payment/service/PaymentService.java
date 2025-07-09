package com.ticketon.ticketon.domain.payment.service;

import com.ticketon.ticketon.domain.payment.dto.*;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.producer.PaymentProducer;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.utils.OptionalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    // 결제 승인 요청
    public void confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
        PaymentConfirmResponse paymentConfirmResponse = paymentGateway.requestPaymentConfirm(paymentConfirmRequest);
        paymentProducer.sendPayment(paymentConfirmResponse,paymentConfirmRequest);
    }

    // 결제 취소 요청
    public void cancelPayment(PaymentCancelRequest paymentCancelRequest) {
        PaymentCancelResponse paymentCancelResponse = paymentGateway.requestPaymentCancel(paymentCancelRequest);
        Payment payment = OptionalUtil.getOrElseThrow(paymentRepository.findByPaymentKey(paymentCancelRequest.getPaymentKey()), "존재하지 않는 예약 정보입니다.");
        payment.cancelPayment(paymentCancelResponse.getCanceledAt().toLocalDateTime());
        paymentRepository.save(payment);
    }

    public PaymentResponse findByTicketTypeId(Long ticketId) {
        Payment payment = OptionalUtil.getOrElseThrow(paymentRepository.findByTicketId(ticketId), "존재하지 않는 결제 정보입니다.");
        return PaymentResponse.toDto(payment);
    }
}
