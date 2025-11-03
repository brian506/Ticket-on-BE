package com.ticketon.ticketon.domain.ticket.service;

import com.ticket.exception.custom.DataNotFoundException;
import com.ticket.exception.custom.ExceededTicketQuantityException;
import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.payment.service.PaymentGateway;
import com.ticketon.ticketon.domain.payment.service.PaymentService;
import com.ticketon.ticketon.domain.ticket.dto.TicketPayload;
import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketStatus;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketResponse;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.domain.ticket.service.strategy.PessimisticLockTicketIssueService;
import com.ticketon.ticketon.domain.ticket.service.strategy.RedisLockTicketIssueService;
import com.ticketon.ticketon.domain.ticket.service.strategy.TicketIssueStrategy;
import com.ticketon.ticketon.utils.OptionalUtil;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

    private final Map<String, TicketIssueStrategy> strategyMap;

    // 원자적 재고 감소 - 상태 PENDING
    public TicketReadyResponse purchaseTicket(TicketRequest ticketRequest){
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findById(ticketRequest.getTicketTypeId()),"존재하지 않는 티켓입니다.");
        Member member = OptionalUtil.getOrElseThrow(memberRepository.findById(ticketRequest.getMemberId()),"존재하지 않는 샤용자입니다.");
        String orderId = new ULID().nextULID();
        // 재고 차감
        int updatedRows = ticketTypeRepository.decreaseTicketAtomically(ticketRequest.getTicketTypeId());

        if (updatedRows == 0){
            throw new ExceededTicketQuantityException(ticketType.getName(),ticketType.getPrice());
        }
        // 상태 PENDING 지정
        Ticket ticket = Ticket.createTicket(ticketType,member);
        ticketRepository.save(ticket);
        return TicketReadyResponse.toDto(ticket,orderId);
    }

    // 티켓 최종 저장
    public void issueTicket(PaymentMessage message){
        Ticket ticket = OptionalUtil.getOrElseThrow(ticketRepository.findById(message.getTicketId()),"존재하지 않는 티켓입니다.");

        if(ticket.getTicketStatus() == TicketStatus.CONFIRMED){
            return;
        }
        if(ticket.getTicketStatus() != TicketStatus.PAID){
            throw new IllegalStateException("결제되지 않은 티켓입니다.");
        }
        Payment payment = message.toEntity(message);
        paymentRepository.save(payment);
        // 최종 승인된 티켓
        ticket.setTicketStatus(TicketStatus.CONFIRMED);
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

