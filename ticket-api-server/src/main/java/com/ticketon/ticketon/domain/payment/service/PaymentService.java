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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final TicketRepository ticketRepository;

    //todo pg 사 호출 - 성공,취소 예외 처리
    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
         return paymentGateway.requestPaymentConfirm(paymentConfirmRequest);

        // 부하테스트용 ( pg 호출 x )
//        String orderId = new ULID().nextULID();
//        PaymentConfirmResponse paymentConfirmResponse = new PaymentConfirmResponse(orderId,10000,"test-key", OffsetDateTime.now(),OffsetDateTime.now());
    }
    @Transactional
    public void savePayment(PaymentConfirmRequest request,PaymentConfirmResponse paymentResponse){
        Ticket ticket = OptionalUtil.getOrElseThrow(ticketRepository.findById(request.getTicketId()),"존재하지 않는 티켓입니다.");

        // PAID 로 상태변경
        int updatedRows = ticketRepository.updateTicketStatus(ticket.getId());

        if(updatedRows == 0){
            throw new DataNotFoundException("존재하지 않거나 만료된 예약입니다.");
        }
        PaymentMessage message = paymentResponse.fromResponse(paymentResponse,ticket);

        savePaymentToOutbox(message);
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

    private void savePaymentToOutbox(PaymentMessage paymentMessage){
        try {
            String jsonPayload = objectMapper.writeValueAsString(paymentMessage);

            OutboxMessage message = OutboxMessage.builder()
                    .topic("ticket-confirm")
                    .payload(jsonPayload)
                    .build();

            // T2 트랜잭션에 포함되어 DB에 저장
            outboxRepository.save(message);

        } catch (JsonProcessingException e) {
            // 직렬화 실패는 심각한 시스템 오류이므로 RuntimeException 처리
            throw new RuntimeException("Kafka payload 직렬화에 실패했습니다.", e);
        }
    }
}
