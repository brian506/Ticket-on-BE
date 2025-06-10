package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketResponse;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.exception.custom.NotFoundDataException;
import com.ticketon.ticketon.utils.SuccessResponse;
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


    public void purchaseTicket(TicketPurchaseRequest request, Long memberId) {

        // 쿼리 날리지 않고 않고 프록시로 조회
        TicketType ticketType = ticketTypeRepository.getReferenceById(request.getTicketTypeId());
        Member member = memberRepository.getReferenceById(memberId);

        List<Ticket> tickets = new ArrayList<>();
        for(int i = 1; i <= request.getQuantity(); i++) {
            tickets.add(Ticket.createNormalTicket(ticketType, member));
            ticketType.increaseIssuedQuantity();
        }

        ticketRepository.saveAll(tickets);

        return;
    }

    // 멤버 티켓 목록
    public List<TicketResponse> findMyTickets(Long memeberId) {
        // 사용자가 소유한 티켓 조회
        List<Ticket> tickets = ticketRepository.findByMemberId(memeberId);

        // dto로 변환 후 반환
        return tickets.stream()
                .map(TicketResponse::from)
                .toList();
    }

    // 멤버 티켓 취소
    public void cancelMyTicket(Long memeberId, Long ticketId) {
        // 취소하려는 티켓 조회
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new NotFoundDataException("취소하려는 티켓을 찾을 수 없습니다."));

        // 취소하려는 티켓이 해당 멤버 티켓이 아니라면
        if (!ticket.getId().equals(ticketId)) throw new RuntimeException("잘못된 접근입니다.");

        // 티켓 취소처리
        ticket.cancel();
        ticket.getTicketType().decreaseTicketQuantity();
        // dirty checking

        return;
    }
}
