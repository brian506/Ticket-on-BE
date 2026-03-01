package com.ticketon.ticketon.domain.payment.service;

import com.ticketon.ticketon.domain.payment.dto.*;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.ticket.dto.TicketPayload;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.repository.TicketRedisRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.utils.OptionalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final TicketRedisRepository redisRepository;
    private final OutboxEventService outboxEventService;

    /**
     * 결제 확인 메시지를 받아 티켓 상태를 PAID로 전환하고 Outbox에 저장합니다.
     */
    @Transactional
    public void confirmPayment(PaymentMessage message) {
        Ticket ticket = ticketRepository.findByOrderId(message.getOrderId()).orElseThrow();
        ticket.markAsPaid();
        outboxEventService.savePaymentToOutbox(new OutboxEvent(message));
    }

    /**
     * Redis에 해당 주문의 티켓 정보가 존재하는 경우에만 결제를 확인합니다.
     */
    public void savePayment(PaymentMessage message) {
        TicketPayload ticketPayload = redisRepository.get(message.getOrderId());
        if (ticketPayload == null) return;
        confirmPayment(message);
    }

    /**
     * PG사에 결제 취소를 요청하고 결제 상태를 CANCELED로 전환합니다.
     */
    @Transactional
    public void cancelPayment(PaymentCancelRequest paymentCancelRequest) {
        PaymentCancelResponse paymentCancelResponse = paymentGateway.requestPaymentCancel(paymentCancelRequest);
        Payment payment = OptionalUtil.getOrElseThrow(
                paymentRepository.findByPaymentKey(paymentCancelRequest.getPaymentKey()),
                "존재하지 않는 예약 정보입니다.");
        payment.cancelPayment(paymentCancelResponse.getCanceledAt().toLocalDateTime());
        paymentRepository.save(payment);
    }

    /**
     * 티켓 ID로 결제 정보를 조회합니다.
     */
    public PaymentResponse findByTicketTypeId(Long ticketId) {
        Payment payment = OptionalUtil.getOrElseThrow(
                paymentRepository.findByTicketId(ticketId),
                "존재하지 않는 결제 정보입니다.");
        return PaymentResponse.toDto(payment);
    }
}
