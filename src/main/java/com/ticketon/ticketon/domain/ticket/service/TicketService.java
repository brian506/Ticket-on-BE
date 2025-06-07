package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequestDto;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.exception.custom.ExceededTicketQuantityException;
import com.ticketon.ticketon.utils.SuccessResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public SuccessResponse purchaseTicket(TicketPurchaseRequestDto ticketPurchaseRequestDto, Long memberId) {

        // 쿼리 날리지 않고 않고 프록시로 조회
        TicketType ticketType = ticketTypeRepository.getReferenceById(ticketPurchaseRequestDto.getTicketTypeId());
        Member member = memberRepository.getReferenceById(memberId);

        // 개수만큼 티켓 생성
        for(Integer i = 1; i <= ticketPurchaseRequestDto.getQuantity(); i++) {
            ticketRepository.save(Ticket.createNormalTicket(ticketType, member));
            ticketType.issueTicket();
        }

        return new SuccessResponse(true, "티켓 구매 성공", null);
    }
}
