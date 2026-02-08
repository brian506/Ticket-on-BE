package com.ticketon.ticketon.domain.payment.service;

import com.ticket.exception.custom.DataNotFoundException;
import com.ticketon.ticketon.domain.payment.dto.OutboxEvent;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmRequest;
import com.ticketon.ticketon.domain.payment.dto.PaymentConfirmResponse;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.ticket.dto.TicketPayload;
import com.ticketon.ticketon.domain.ticket.repository.TicketRedisRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PaymentGatewayService {


    private final TicketRedisRepository ticketRedisRepository;
    private final OutboxEventService outboxEventService;
    private final PaymentService paymentService;


    //todo pg 사 호출 - 성공,취소 예외 처리
    public PaymentMessage confirmPayment(PaymentConfirmRequest paymentConfirmRequest) {
        TicketPayload ticketPayload = ticketRedisRepository.get(paymentConfirmRequest.getOrderId());
        if (ticketPayload == null) {
            throw new DataNotFoundException("시간이 지나 만료된 요청입니다. 결제를 진행할 수 없습니다.");
        }
        // 부하테스트용 ( pg 호출 x )
        PaymentConfirmResponse response = new PaymentConfirmResponse(paymentConfirmRequest.getOrderId(), 10000, "test-key", OffsetDateTime.now(), OffsetDateTime.now());
//        PaymentConfirmResponse response = paymentGateway.requestPaymentConfirm(paymentConfirmRequest);
        PaymentMessage message = response.fromResponse(paymentConfirmRequest);
        // 재고 결제 완료 상태 저장
        ticketRedisRepository.savePaidTicket(paymentConfirmRequest.getOrderId());
        outboxEventService.savePaymentToOutbox(new OutboxEvent(message));
//        paymentService.savePayment(message);

        return message;
    }
}
