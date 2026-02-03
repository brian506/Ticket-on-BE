package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.payment.dto.OutboxEvent;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.payment.service.OutboxEventService;
import com.ticketon.ticketon.domain.ticket.dto.TicketPayload;
import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketStatus;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketResponse;
import com.ticketon.ticketon.domain.ticket.repository.TicketRedisRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.domain.ticket.service.strategy.TicketIssueStrategy;
import com.ticketon.ticketon.utils.OptionalUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketIssueStrategy ticketIssueStrategy;
    private final PaymentRepository paymentRepository;


    // 원자적 재고 감소 - 상태 PENDING
    @Transactional
    public TicketReadyResponse purchaseTicket(TicketRequest ticketRequest, String orderId){
        Ticket ticket = ticketIssueStrategy.purchaseTicketAtomic(ticketRequest.getTicketTypeId(),ticketRequest.getMemberId(), orderId);
        ticketRepository.save(ticket);
        log.info("[Ticket] 티켓 요청 성공 {}", ticket.getOrderId());
        return TicketReadyResponse.toDto(ticket,ticket.getOrderId());
    }


    // 티켓 최종 저장
    @Transactional
    public void issueTicket(List<PaymentMessage> messages){

        List<String> orderIds = messages.stream()
                .map(PaymentMessage::getOrderId)
                .toList();

        Map<String, Ticket> ticketMap = ticketRepository.findAllByOrderIdIn(orderIds)
                .stream()
                .collect(Collectors.toMap(Ticket::getOrderId, Function.identity()));

        List<Payment> payments = new ArrayList<>();

        for(PaymentMessage message : messages) {
            Ticket ticket = ticketMap.get(message.getOrderId());

            if (ticket.getTicketStatus() == TicketStatus.PENDING) {
                Payment payment = message.toEntity(message);
                payments.add(payment);
                // 최종 승인된 티켓
                ticket.setTicketStatus(TicketStatus.CONFIRMED);
            }
        }
        paymentRepository.saveAll(payments);
        log.info("[Ticket] 티켓 최종 저장 성공");
    }


    public TicketRequest requestTicket(TicketPurchaseRequest request, Long memberId) {
        TicketType ticketType = ticketTypeRepository.getReferenceById(request.getTicketTypeId());
        return TicketRequest.from(memberId, ticketType);
    }


    // 멤버 티켓 목록
    public List<TicketResponse> findMyTickets(Long memberId) {
        List<Ticket> tickets = ticketRepository.findByMember_Id(memberId);
        return tickets.stream()
                .map(TicketResponse::from)
                .toList();
    }

    // 멤버 티켓 취소
    public void cancelMyTicket(Long memberId, Long ticketId) {
        Ticket ticket = OptionalUtil.getOrElseThrow(ticketRepository.findById(ticketId), "취소하려는 티켓을 찾을 수 없습니다.");
        if (!ticket.getId().equals(ticketId)) throw new RuntimeException("잘못된 접근입니다.");
        ticket.cancel();
        ticket.getTicketType().decreaseTicketQuantity();
    }
}

