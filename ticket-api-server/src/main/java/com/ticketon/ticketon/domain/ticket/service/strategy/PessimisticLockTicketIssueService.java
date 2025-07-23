package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
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

@Service(TicketIssueStrategyType.PESSIMISTIC_STRATEGY_NAME)
@Qualifier(TicketIssueStrategyType.PESSIMISTIC_STRATEGY_NAME)
@RequiredArgsConstructor
public class PessimisticLockTicketIssueService implements TicketIssueStrategy {

    private final TicketTypeRepository ticketTypeRepository;
    private final TicketRepository ticketRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override // (1)
    public TicketType purchaseTicket(PaymentMessage message, Long memberId) {
        Long ticketTypeId = message.getTicketTypeId();
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findByIdForUpdate(ticketTypeId), "티켓 타입 조회 실패 ticket_id=" + ticketTypeId);

        if (ticketType.getIssuedQuantity() >= ticketType.getMaxQuantity()) {
            throw new IllegalStateException("티켓이 모두 소진되었습니다.");
        }
        return ticketType;


    }
}
