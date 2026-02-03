package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticket.exception.custom.ExceededTicketQuantityException;
import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.ticket.dto.NewTicketEvent;
import com.ticketon.ticketon.domain.ticket.dto.TicketReadyResponse;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.utils.OptionalUtil;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketIssueStrategy {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final MemberRepository memberRepository;

    // 낙관락
    @Transactional
    public Ticket purchaseTicketByOptimisticLock(Long ticketTypeId, Long memberId,String orderId) {
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findById(ticketTypeId),"존재하지 않는 티켓입니다.");
        Member member = OptionalUtil.getOrElseThrow(memberRepository.findById(memberId),"존재하지 않는 회원입니다.");
//        ticketType.increaseIssuedQuantity();
        return Ticket.createTicket(ticketType,member,orderId);
    }


    public synchronized Ticket purchaseTicketBySynchronize(Long ticketTypeId, Long memberId,String orderId) {
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findById(ticketTypeId),"존재하지 않는 티켓입니다.");
        Member member = OptionalUtil.getOrElseThrow(memberRepository.findById(memberId),"존재하지 않는 회원입니다.");
        ticketType.increaseIssuedQuantity();
        ticketTypeRepository.saveAndFlush(ticketType); // 더티체킹 안되서 직접 저장
        return Ticket.createTicket(ticketType,member,orderId);
    }

    // 비관락
    @Transactional
    public Ticket purchaseTicketByPessimisticLock(Long ticketTypeId, Long memberId,String orderId) {
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findByIdForUpdate(ticketTypeId),"존재하지 않는 티켓입니다.");
        Member member = OptionalUtil.getOrElseThrow(memberRepository.findById(memberId),"존재하지 않는 회원입니다.");
        ticketType.increaseIssuedQuantity();
        return Ticket.createTicket(ticketType,member,orderId);
    }

    // 원자적 연산
    @Transactional
    public Ticket purchaseTicketAtomic(Long ticketTypeId, Long memberId,String orderId) {
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findById(ticketTypeId),"존재하지 않는 티켓입니다.");
        Member member = OptionalUtil.getOrElseThrow(memberRepository.findById(memberId),"존재하지 않는 회원입니다.");
        long updatedRows = ticketTypeRepository.increaseTicketAtomically(ticketTypeId);

        if (updatedRows == 0){
            throw new ExceededTicketQuantityException(ticketType.getName(),ticketType.getPrice());
        }
        return Ticket.createTicket(ticketType,member,orderId);
    }

    @Transactional
    public TicketReadyResponse purchaseTicketRedis(Long ticketTypeId, Long memberId,String orderId) {
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findById(ticketTypeId),"존재하지 않는 티켓입니다.");
        Member member = OptionalUtil.getOrElseThrow(memberRepository.findById(memberId),"존재하지 않는 회원입니다.");
        ticketType.increaseIssuedQuantity();
        Ticket ticket = Ticket.createTicket(ticketType, member, orderId);
        ticketRepository.save(ticket);
        return TicketReadyResponse.toDto(ticket, orderId);
    }

    public TicketReadyResponse purchaseTicketRedisLua(Long ticketTypeId, Long memberId,String orderId) {
        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findById(ticketTypeId),"존재하지 않는 티켓입니다.");
        Member member = OptionalUtil.getOrElseThrow(memberRepository.findById(memberId),"존재하지 않는 회원입니다.");
        Ticket ticket = Ticket.createTicket(ticketType, member, orderId);
        ticketRepository.save(ticket);
        return TicketReadyResponse.toDto(ticket, orderId);
    }

    @Transactional
    public void requestTicketKafka(List<NewTicketEvent> events) {
        List<Long> ticketTypeIds = events.stream()
                .map(NewTicketEvent::getTicketTypeId)
                .toList();

        List<Long> memberIds = events.stream()
                .map(NewTicketEvent::getMemberId)
                .toList();

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
                log.error("존재하지 않는 TicketType ID: {}", event.getTicketTypeId());
                continue;
            }
            if (member == null) {
                log.error("존재하지 않는 Member ID: {}", event.getMemberId());
                continue;
            }
            tickets.add(Ticket.createTicket(ticketType, member, event.getOrderId()));
        }
        ticketRepository.saveAll(tickets);
    }

}
