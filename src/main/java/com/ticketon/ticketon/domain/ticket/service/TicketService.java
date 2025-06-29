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
import com.ticketon.ticketon.domain.payment.producer.PaymentProducer;
import com.ticketon.ticketon.utils.OptionalUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final MemberRepository memberRepository;

    public void findTicketsByEventId(final Long eventId){

    }

    public TicketRequest purchaseTicket(TicketPurchaseRequest request, Long memberId) {
        TicketType ticketType = ticketTypeRepository.getReferenceById(request.getTicketTypeId());
        return TicketRequest.toDto(memberId,ticketType);
    }

    public void saveTicketInfo(PaymentMessage message, Long memberId) {
        TicketType ticketType = ticketTypeRepository.getReferenceById(message.getTicketTypeId());
        Member member = memberRepository.getReferenceById(memberId);
        Ticket ticket = Ticket.createNormalTicket(ticketType, member);
        ticketType.increaseIssuedQuantity();
        ticketRepository.save(ticket);
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

