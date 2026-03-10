package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.domain.ticket.service.TicketIssueStrategy;
import com.ticketon.ticketon.utils.OptionalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PessimisticLockTicketIssueStrategy implements TicketIssueStrategy {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public TicketReadyResponse issue(TicketRequest request, String orderId) {
        TicketType ticketType = OptionalUtil.getOrElseThrow(
                ticketTypeRepository.findByIdForUpdate(request.getTicketTypeId()),
                "존재하지 않는 티켓입니다.");
        Member member = OptionalUtil.getOrElseThrow(
                memberRepository.findById(request.getMemberId()),
                "존재하지 않는 회원입니다.");

        ticketType.increaseIssuedQuantity();
        Ticket ticket = Ticket.createTicket(ticketType, member, orderId);
        ticketRepository.save(ticket);
        return TicketReadyResponse.toDto(ticket, orderId);
    }
}
