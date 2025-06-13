package com.ticketon.ticketon.domain.payment.service;

import com.ticketon.ticketon.domain.payment.config.PaymentClient;
import com.ticketon.ticketon.domain.payment.dto.*;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.entity.PaymentStatus;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.exception.payment.PaymentCancelException;
import com.ticketon.ticketon.exception.payment.PaymentConfirmException;
import com.ticketon.ticketon.utils.OptionalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    // 결제 승인 요청
    public Payment confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
        PaymentConfirmResponse paymentConfirmResponse = paymentClient.confirmPayment(paymentConfirmRequest);

            Payment payment = Payment.builder()
                    .ticketId(Long.valueOf(paymentConfirmRequest.getTicketId()))
                    .memberId(paymentConfirmRequest.getMemberId())
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .amount(paymentConfirmRequest.getAmount())
                    .paymentKey(paymentConfirmResponse.getPaymentKey())
                    .requestedAt(paymentConfirmResponse.getRequestedAt().toLocalDateTime())
                    .approvedAt(paymentConfirmResponse.getApprovedAt().toLocalDateTime())
                    .build();
            return paymentRepository.save(payment);

    }

    // 결제 취소 요청
    public void cancelPayment(PaymentCancelRequest paymentCancelRequest) {
        PaymentCancelResponse paymentCancelResponse = paymentClient.cancelPayment(paymentCancelRequest);

        Payment payment = OptionalUtil.getOrElseThrow(paymentRepository.findByPaymentKey(paymentCancelRequest.getPaymentKey()),"존재하지 않는 예약 정보입니다.");

        payment.setPaymentStatus(PaymentStatus.CANCELED);
        payment.setCanceledAt(paymentCancelResponse.getCanceledAt().toLocalDateTime());
        paymentRepository.save(payment);
    }

    public PaymentResponse findByTicketId(Long ticketId) {
        Payment payment = OptionalUtil.getOrElseThrow(paymentRepository.findByTicketId(ticketId),"존재하지 않는 결제 정보입니다.");
        return PaymentResponse.toDto(payment);
    }

}
