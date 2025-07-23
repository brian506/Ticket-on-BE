//package com.ticketon.ticketon.domain.ticket.service.strategy;
//
//import com.ticketon.ticketon.domain.member.entity.Member;
//import com.ticketon.ticketon.domain.member.repository.MemberRepository;
//import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
//import com.ticketon.ticketon.domain.ticket.entity.Ticket;
//import com.ticketon.ticketon.domain.ticket.entity.TicketType;
//import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
//import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
//import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//public class PessimisticLickTicketIssueServiceTest {
//
//
//    @InjectMocks
//    private PessimisticLockTicketIssueService service;
//
//    @Mock
//    private TicketTypeRepository ticketTypeRepository;
//
//    @Mock
//    private TicketRepository ticketRepository;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Test
//    void purchaseTicket_success() {
//        // Given
//        Long ticketTypeId = 1L;
//        Long memberId = 2L;
//
//        TicketPurchaseRequest request = new TicketPurchaseRequest();
//        request.setTicketTypeId(ticketTypeId);
//        request.setQuantity(1); // 실제로는 사용 안 하지만 넣어둠
//
//        TicketType mockTicketType = mock(TicketType.class);
//        Member mockMember = mock(Member.class);
//        Ticket mockTicket = mock(Ticket.class);
//
//        given(ticketTypeRepository.findByIdForUpdate(ticketTypeId))
//                .willReturn(Optional.of(mockTicketType));
//
//        given(memberRepository.getReferenceById(memberId))
//                .willReturn(mockMember);
//
////        given(mockTicketType.getEventItem()).willReturn(null); // from()에서 내부적으로 사용 가능
////        given(mockTicketType.getId()).willReturn(ticketTypeId);
////        given(mockTicketType.getPrice()).willReturn(10000L);
////        given(mockTicketType.getName()).willReturn("티켓");
//
//        given(mockTicketType.getIssuedQuantity()).willReturn(1L);
//        given(mockTicketType.getMaxQuantity()).willReturn(1000L);
//
//        given(ticketRepository.save(any(Ticket.class)))
//                .willReturn(mockTicket);
//
//        // When
//        TicketRequest ticketRequest = service.purchaseTicket(request, memberId);
//
//        // Then
//        assertThat(ticketRequest.getMemberId()).isEqualTo(memberId);
//        assertThat(ticketRequest.getTicketTypeId()).isEqualTo(ticketTypeId);
//
//        verify(ticketTypeRepository).findByIdForUpdate(ticketTypeId);
//        verify(memberRepository).getReferenceById(memberId);
//        verify(mockTicketType).increaseIssuedQuantity();
//        verify(ticketRepository).save(any(Ticket.class));
//    }
//}
