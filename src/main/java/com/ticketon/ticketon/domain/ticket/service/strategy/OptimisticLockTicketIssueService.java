package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.utils.OptionalUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service(TicketIssueStrategyType.OPTIMISTIC_STRATEGY_NAME)
@Qualifier(TicketIssueStrategyType.OPTIMISTIC_STRATEGY_NAME)
@RequiredArgsConstructor
public class OptimisticLockTicketIssueService implements TicketIssueStrategy {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public TicketRequest purchaseTicket(TicketPurchaseRequest request, Long memberId) {
        Long ticketTypeId = request.getTicketTypeId();
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findById(ticketTypeId), "티켓 타입 조회 실패 ticket_id=" + ticketTypeId);

        Member member = memberRepository.getReferenceById(memberId);

        ticketType.increaseIssuedQuantity(); // @Version으로 낙관적 락 처리됨

        Ticket ticket = Ticket.createNormalTicket(ticketType, member);
        ticketRepository.save(ticket);

        return TicketRequest.from(memberId, ticketType);
    }

}
