package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticket.exception.custom.ExceededTicketQuantityException;
import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.utils.OptionalUtil;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TicketIssueStrategy {

    private final TicketTypeRepository ticketTypeRepository;
    private final MemberRepository memberRepository;
    private static final String orderId = new ULID().nextULID();

    // 낙관락
    @Transactional
    public Ticket purchaseTicketByOptimisticLock(Long ticketTypeId, Long memberId) {
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findById(ticketTypeId),"존재하지 않는 티켓입니다.");
        Member member = OptionalUtil.getOrElseThrow(memberRepository.findById(memberId),"존재하지 않는 회원입니다.");
        ticketType.increaseIssuedQuantity();
        return Ticket.createTicket(ticketType,member,orderId);
    }


    public synchronized Ticket purchaseTicketBySynchronize(Long ticketTypeId, Long memberId) {
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findById(ticketTypeId),"존재하지 않는 티켓입니다.");
        Member member = OptionalUtil.getOrElseThrow(memberRepository.findById(memberId),"존재하지 않는 회원입니다.");
        ticketType.increaseIssuedQuantity();
        ticketTypeRepository.saveAndFlush(ticketType); // 더티체킹 안되서 직접 저장
        return Ticket.createTicket(ticketType,member,orderId);
    }

    // 비관락
    @Transactional
    public Ticket purchaseTicketByPessimisticLock(Long ticketTypeId, Long memberId) {
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findByIdForUpdate(ticketTypeId),"존재하지 않는 티켓입니다.");
        Member member = OptionalUtil.getOrElseThrow(memberRepository.findById(memberId),"존재하지 않는 회원입니다.");
        ticketType.increaseIssuedQuantity();
        return Ticket.createTicket(ticketType,member,orderId);
    }

    // 원자적 연산
    @Transactional
    public Ticket purchaseTicketAtomic(Long ticketTypeId, Long memberId) {
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findById(ticketTypeId),"존재하지 않는 티켓입니다.");
        Member member = OptionalUtil.getOrElseThrow(memberRepository.findById(memberId),"존재하지 않는 회원입니다.");
        long updatedRows = ticketTypeRepository.increaseTicketAtomically(ticketTypeId);

        if (updatedRows == 0){
            throw new ExceededTicketQuantityException(ticketType.getName(),ticketType.getPrice());
        }
        return Ticket.createTicket(ticketType,member,orderId);
    }

}
