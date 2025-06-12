package com.ticketon.ticketon.domain.payment.service;

import com.ticketon.ticketon.domain.payment.config.PaymentClient;
import com.ticketon.ticketon.domain.payment.dto.PaymentRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentResponse;
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
    public Payment confirmPayment(PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = paymentClient.confirmPayment(paymentRequest);

        try {
            Payment payment = Payment.builder()
                    .ticketId(paymentRequest.getTicketId())
                    .userId(paymentRequest.getUserId())
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .amount(paymentResponse.getAmount())
                    .paymentKey(paymentResponse.getPaymentKey())
                    .requestedAt(paymentResponse.getRequestedAt())
                    .approvedAt(paymentResponse.getApprovedAt())
                    .build();
            return paymentRepository.save(payment);
        }
        catch (Exception e){
            throw new PaymentConfirmException(e);
        }

    }
}
