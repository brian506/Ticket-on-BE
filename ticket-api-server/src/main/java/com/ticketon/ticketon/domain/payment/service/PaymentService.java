package com.ticketon.ticketon.domain.payment.service;

import com.ticketon.ticketon.domain.payment.dto.*;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.producer.PaymentProducer;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import com.ticketon.ticketon.utils.OptionalUtil;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    public void confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
         PaymentConfirmResponse paymentConfirmResponse = paymentGateway.requestPaymentConfirm(paymentConfirmRequest);

        // 부하테스트용 ( pg 호출 x )
//        String orderId = new ULID().nextULID();
//        PaymentConfirmResponse paymentConfirmResponse = new PaymentConfirmResponse(orderId,10000,"test-key", OffsetDateTime.now(),OffsetDateTime.now());

        paymentProducer.sendPayment(paymentConfirmResponse,paymentConfirmRequest);
    }

    public void savePaymentsByTickets(List<Ticket> savedTickets,List<PaymentMessage> messages) {
        // ticket 에 저장된 orderId 로 message key 값 설정
        Map<String, PaymentMessage> messageMap = messages.stream()
                .collect(Collectors.toMap(PaymentMessage::getOrderId, message -> message,
                        (existingMessage, newMessage) -> existingMessage));

        List<Payment> payments = savedTickets.stream()
                .map(ticket -> {
                    // ticket 의 고유한 orderId 로 message 가져와서 payment 객체 저장
                    PaymentMessage message = messageMap.get(ticket.getOrderId());
                    Payment payment = message.toEntity(message);
                    payment.setTicketId(ticket.getId());
                    return payment;
                })
                .collect(Collectors.toList());

        paymentRepository.saveAll(payments);
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
