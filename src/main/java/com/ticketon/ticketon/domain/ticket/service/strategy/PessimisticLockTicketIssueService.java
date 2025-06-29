package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.exception.custom.DataNotFoundException;
import com.ticketon.ticketon.utils.OptionalUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service(TicketIssueStrategyType.PESSIMISTIC_STRATEGY_NAME)
@Qualifier(TicketIssueStrategyType.PESSIMISTIC_STRATEGY_NAME)
@RequiredArgsConstructor
public class PessimisticLockTicketIssueService implements TicketIssueStrategy {

    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void purchaseTicket(TicketPurchaseRequest request, Long memberId) {
        Long ticketTypeId = request.getTicketTypeId();
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findByIdForUpdate(ticketTypeId), "티켓 타입 조회 실패 ticket_id=" + ticketTypeId);
;
        // 쿼리 날리지 않고 프록시로 조회
        Member member = memberRepository.getReferenceById(memberId);

        ticketType.increaseIssuedQuantity();

        Ticket ticket = Ticket.createNormalTicket(ticketType, member);
        ticketRepository.save(ticket);

    }
}
