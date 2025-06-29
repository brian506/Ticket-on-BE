package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketResponse;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
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
    private final RedisTemplate<String, String> redisTemplate;

    private final Map<String, TicketIssueStrategy> strategyMap;


    public void findTicketsByEventId(final Long eventId){

    }

    public void purchaseTicket(String strategyType, TicketPurchaseRequest request, Long memberId) {

        TicketIssueStrategy strategy = strategyMap.get(strategyType);
        if (strategy == null) {
            throw new IllegalArgumentException("존재하지 않는 전략 타입: " + strategyType);
        }
        strategy.purchaseTicket(request, memberId);

        redisTemplate.delete("allowed:" + memberId);
    }

    // 멤버 티켓 목록
    public List<TicketResponse> findMyTickets(Long memberId) {
        List<Ticket> tickets = ticketRepository.findByMemberId(memberId);
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

