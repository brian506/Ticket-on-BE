package com.ticketon.ticketon.domain.payment.service;

import com.ticketon.ticketon.domain.payment.config.PaymentClient;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmResponse;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.entity.PaymentStatus;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.exception.payment.PaymentConfirmException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    // 결제 승인 요청
    public Payment confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
        PaymentConfirmResponse paymentConfirmResponse = paymentClient.confirmPayment(paymentConfirmRequest);

        try {
            Payment payment = Payment.builder()
                    .ticketId(paymentConfirmRequest.getTicketId())
                    .userId(paymentConfirmRequest.getUserId())
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .amount(paymentConfirmResponse.getAmount())
                    .paymentKey(paymentConfirmResponse.getPaymentKey())
                    .requestedAt(paymentConfirmResponse.getRequestedAt())
                    .approvedAt(paymentConfirmResponse.getApprovedAt())
                    .build();
            return paymentRepository.save(payment);
        }
        catch (Exception e){
            throw new PaymentConfirmException(e);
        }

    }
}
