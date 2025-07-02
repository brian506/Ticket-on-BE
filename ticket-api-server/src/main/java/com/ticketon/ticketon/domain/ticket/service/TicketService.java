package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketResponse;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.domain.ticket.service.strategy.TicketIssueStrategy;
import com.ticketon.ticketon.utils.OptionalUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private final Map<String, TicketIssueStrategy> strategyMap;

    public TicketRequest requestTicket(TicketPurchaseRequest request, Long memberId) {
        TicketType ticketType = ticketTypeRepository.getReferenceById(request.getTicketTypeId());
        return TicketRequest.from(memberId, ticketType);
    }

    public void saveTicketInfo(PaymentMessage message, Long memberId) {
        TicketType ticketType = ticketTypeRepository.getReferenceById(message.getTicketTypeId());
        Member member = memberRepository.getReferenceById(memberId);
        Ticket ticket = Ticket.createNormalTicket(ticketType, member);
        ticketType.increaseIssuedQuantity();
        ticketRepository.save(ticket);
    }

    public TicketRequest purchaseTicket(String strategyType, TicketPurchaseRequest request, Long memberId) {

        TicketIssueStrategy strategy = strategyMap.get(strategyType);
        if (strategy == null) {
            throw new IllegalArgumentException("존재하지 않는 전략 타입: " + strategyType);
        }

        TicketRequest ticketRequest = strategy.purchaseTicket(request, memberId);
        redisTemplate.delete("allowed:" + memberId);

        return ticketRequest;
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

