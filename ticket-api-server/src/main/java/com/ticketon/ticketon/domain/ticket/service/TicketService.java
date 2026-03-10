package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.ticket.dto.ExpiredTicket;
import com.ticketon.ticketon.domain.ticket.dto.NewTicketEvent;
import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketStatus;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketResponse;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.utils.OptionalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final StringRedisTemplate redisTemplate;
    private final TicketIssueStrategy ticketIssueStrategy;
    private final TicketExpiryProcessor ticketExpiryProcessor;

    public TicketReadyResponse purchaseTicket(TicketRequest ticketRequest, String orderId) {
        TicketReadyResponse response = ticketIssueStrategy.issue(ticketRequest, orderId);
        log.info("[Ticket] 티켓 요청 성공: orderId={}", orderId);
        return response;
    }

    @Transactional
    public void issueTicket(List<PaymentMessage> messages) {
        List<String> orderIds = messages.stream()
                .map(PaymentMessage::getOrderId)
                .toList();

        Map<String, Ticket> ticketMap = ticketRepository.findAllByOrderIdIn(orderIds)
                .stream()
                .collect(Collectors.toMap(Ticket::getOrderId, Function.identity()));

        List<Payment> payments = new ArrayList<>();

        for (PaymentMessage message : messages) {
            Ticket ticket = ticketMap.get(message.getOrderId());
            if (ticket == null) {
                log.warn("[Ticket] 티켓을 찾을 수 없습니다: orderId={}", message.getOrderId());
                continue;
            }
            if (ticket.getTicketStatus() == TicketStatus.PENDING) {
                payments.add(message.toEntity(message, ticket));
                ticket.markAsConfirmed();
            }
        }
        paymentRepository.saveAll(payments);
        log.info("[Ticket] 티켓 최종 저장 성공: {}건", payments.size());
    }

    @Transactional
    public void issueTicketBatch(List<NewTicketEvent> events) {
        List<Long> ticketTypeIds = events.stream().map(NewTicketEvent::getTicketTypeId).toList();
        List<Long> memberIds = events.stream().map(NewTicketEvent::getMemberId).toList();

        Map<Long, TicketType> ticketTypeMap = ticketTypeRepository.findAllById(ticketTypeIds)
                .stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));

        Map<Long, Member> memberMap = memberRepository.findAllById(memberIds)
                .stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        List<Ticket> tickets = new ArrayList<>();
        for (NewTicketEvent event : events) {
            TicketType ticketType = ticketTypeMap.get(event.getTicketTypeId());
            Member member = memberMap.get(event.getMemberId());

            if (ticketType == null) {
                log.error("[TicketBatch] 존재하지 않는 TicketType ID: {}", event.getTicketTypeId());
                continue;
            }
            if (member == null) {
                log.error("[TicketBatch] 존재하지 않는 Member ID: {}", event.getMemberId());
                continue;
            }
            tickets.add(Ticket.createTicket(ticketType, member, event.getOrderId()));
        }
        ticketRepository.saveAll(tickets);
        log.info("[TicketBatch] 티켓 배치 저장 완료: {}건", tickets.size());
    }

    public void removePendingTickets() {
        LocalDateTime now = LocalDateTime.now();

        List<ExpiredTicket> expiredTickets = findExpiredTickets(now);
        if (expiredTickets.isEmpty()) return;

        List<Long> cancelTicketIds = new ArrayList<>();
        List<ExpiredTicket> ticketsToRestore = new ArrayList<>();

        for (ExpiredTicket ticket : expiredTickets) {
            boolean isPaid = Boolean.TRUE.equals(redisTemplate.hasKey("payment_success:" + ticket.orderId()));
            if (isPaid) continue;

            cancelTicketIds.add(ticket.ticketId());
            ticketsToRestore.add(ticket);
        }

        if (!cancelTicketIds.isEmpty()) {
            ticketExpiryProcessor.processExpiredTickets(cancelTicketIds, now, ticketsToRestore);
        }
        log.info("[Ticket] 결제 미완료 재고 복구 완료: {}건", cancelTicketIds.size());
    }

    public List<ExpiredTicket> findExpiredTickets(LocalDateTime now) {
        return ticketRepository.findExpiredTickets(now);
    }

    public TicketRequest requestTicket(TicketPurchaseRequest request, Long memberId) {
        TicketType ticketType = ticketTypeRepository.getReferenceById(request.getTicketTypeId());
        return TicketRequest.from(memberId, ticketType);
    }

    public List<TicketResponse> findMyTickets(Long memberId) {
        return ticketRepository.findByMember_Id(memberId)
                .stream()
                .map(TicketResponse::from)
                .toList();
    }

    @Transactional
    public void cancelMyTicket(Long memberId, Long ticketId) {
        Ticket ticket = OptionalUtil.getOrElseThrow(ticketRepository.findById(ticketId), "취소하려는 티켓을 찾을 수 없습니다.");
        ticket.cancel();
        ticket.getTicketType().decreaseTicketQuantity();
    }
}
