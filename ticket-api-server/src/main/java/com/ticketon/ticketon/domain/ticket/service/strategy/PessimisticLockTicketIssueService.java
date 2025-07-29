package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.utils.OptionalUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Slf4j
@Service(TicketIssueStrategyType.PESSIMISTIC_STRATEGY_NAME)
@Qualifier(TicketIssueStrategyType.PESSIMISTIC_STRATEGY_NAME)
@RequiredArgsConstructor
public class PessimisticLockTicketIssueService implements TicketIssueStrategy {

    private final TicketTypeRepository ticketTypeRepository;

    @Transactional
    @Override // (1)
    public TicketType purchaseTicket(PaymentMessage message, Long memberId) {
        Long ticketTypeId = message.getTicketTypeId();
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findByIdForUpdate(ticketTypeId), "티켓 타입 조회 실패 ticket_id=" + ticketTypeId);
        log.info("🚨 DB 조회 직후: issued={}, max={}", ticketType.getIssuedQuantity(), ticketType.getMaxQuantity());
        ticketType.validateCanIssueTicket();
        return ticketType;


    }
}
