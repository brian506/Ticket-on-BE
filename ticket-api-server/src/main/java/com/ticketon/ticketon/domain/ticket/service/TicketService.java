package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.ticket.dto.TicketPayload;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketResponse;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.domain.ticket.service.strategy.PessimisticLockTicketIssueService;
import com.ticketon.ticketon.domain.ticket.service.strategy.RedisLockTicketIssueService;
import com.ticketon.ticketon.domain.ticket.service.strategy.TicketIssueStrategy;
import com.ticketon.ticketon.utils.OptionalUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final MemberRepository memberRepository;

    private final Map<String, TicketIssueStrategy> strategyMap;

    public TicketRequest requestTicket(TicketPurchaseRequest request, Long memberId) {
        TicketType ticketType = ticketTypeRepository.getReferenceById(request.getTicketTypeId());
        return TicketRequest.from(memberId, ticketType);
    }

    public List<Ticket> createTicketInfoInBatch(TicketType ticketType, List<PaymentMessage> paymentMessages){
        // 메시지에서 memberId 가져옴
       List<Long> memberIds = paymentMessages.stream()
               .map(PaymentMessage::getMemberId)
               .distinct() // 중복 멤버 제거
               .collect(Collectors.toList());
        // db 안에 있는 memberId와 동일한 id 로 memberMap에 저장
       Map<Long,Member> memberMap = memberRepository.findAllById(memberIds).stream()
               .collect(Collectors.toMap(Member::getId,member -> member));

       List<Ticket> tickets = new ArrayList<>();
       for(PaymentMessage message : paymentMessages){
           if(ticketType.getAvailableQuantity() > 0){
               Member member = memberMap.get(message.getMemberId());
               Ticket ticket = Ticket.createNormalTicket(ticketType,member,message.getOrderId());
               tickets.add(ticket);
               ticketType.increaseIssuedQuantity();
           }
       }
       return tickets;
    }
    public List<Ticket> saveAll(List<Ticket> tickets){
        return ticketRepository.saveAll(tickets);
    }

//    public Ticket saveTicketInfo(PaymentMessage message, Long memberId) {
//
//        TicketType ticketType = redisService.purchaseTicket(message,memberId); // 레디스 + 비관락
//        //TicketType ticketType = pessimisticService.purchaseTicket(message,memberId); // 비관락
//
//        Member member = OptionalUtil.getOrElseThrow(memberRepository.findById(memberId),"존재하지 않는 회원입니다");
//        Ticket ticket = Ticket.createNormalTicket(ticketType, member);
//
//        ticketType.increaseIssuedQuantity();
//        return ticketRepository.save(ticket);
//    }

    // 멤버 티켓 목록
    public List<TicketResponse> findMyTickets(Long memberId) {
        List<Ticket> tickets = ticketRepository.findByMember_Id(memberId);
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

