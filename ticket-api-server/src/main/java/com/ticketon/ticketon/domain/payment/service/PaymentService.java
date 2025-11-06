package com.ticketon.ticketon.domain.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.exception.custom.DataNotFoundException;
import com.ticketon.ticketon.domain.payment.dto.*;
import com.ticketon.ticketon.domain.payment.entity.OutboxMessage;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.repository.OutboxRepository;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.utils.OptionalUtil;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.swing.text.html.Option;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public void savePayment(PaymentConfirmRequest request,PaymentMessage message){
        Ticket ticket = OptionalUtil.getOrElseThrow(ticketRepository.findById(request.getTicketId()),"존재하지 않는 티켓입니다.");
        // PAID 로 상태변경
        int updatedRows = ticketRepository.updateTicketStatus(ticket.getId());
        if(updatedRows == 0){
            throw new DataNotFoundException("존재하지 않거나 만료된 예약입니다.");
        }
        message.setExpiredAt(ticket.getExpiredAt());
        eventPublisher.publishEvent(new OutboxEvent(message));
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
