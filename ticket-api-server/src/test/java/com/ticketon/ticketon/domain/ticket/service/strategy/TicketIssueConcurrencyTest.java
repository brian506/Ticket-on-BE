//package com.ticketon.ticketon.domain.ticket.service.strategy;
//
//import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
//import com.ticketon.ticketon.domain.eventitem.entity.EventItemStatus;
//import com.ticketon.ticketon.domain.eventitem.repository.EventItemRepository;
//import com.ticketon.ticketon.domain.member.entity.Member;
//import com.ticketon.ticketon.domain.member.repository.MemberRepository;
//import com.ticketon.ticketon.domain.ticket.entity.TicketType;
//import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
//import com.ticketon.ticketon.domain.ticket.entity.dto.TicketTypeStatus;
//import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
//import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
//import com.ticketon.ticketon.domain.ticket.service.TicketService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertAll;
//
//@SpringBootTest
//public class TicketIssueConcurrencyTest {
//
//    @Autowired private TicketService ticketService;
//    @Autowired private TicketRepository ticketRepository;
//    @Autowired private TicketTypeRepository ticketTypeRepository;
//    @Autowired private MemberRepository memberRepository;
//    @Autowired private EventItemRepository eventItemRepository;
//
//    AtomicInteger failCount = new AtomicInteger();
//
//
//    private final int THREAD_COUNT = 50;
//    private Long memberId = 1l;
//    private Long ticketTypeId = 1l;
//
//
//    @BeforeEach
//    public void setUp() {
//
//        ticketRepository.deleteAll();
//
//        Member member = memberRepository.save(Member.builder()
//                .email("user1@user.com")
//                .password("1234")
//                .build());
//        memberId = member.getId();
//
//        EventItem eventItem = eventItemRepository.save(EventItem.builder()
//                .title("이벤트1")
//                .startDate(LocalDate.now())
//                .endDate(LocalDate.now())
//                .eventItemStatus(EventItemStatus.OPEN)
//                .build());
//
//        TicketType ticketType = ticketTypeRepository.save(TicketType.builder()
//                .eventItem(eventItem)
//                .maxQuantity(1000L)
//                .issuedQuantity(0L)
//                .name("티켓")
//                .price(10000)
//                .status(TicketTypeStatus.ON_SALE)
//                .build());
//        ticketTypeId = ticketType.getId();
//    }
//
//    @AfterEach
//    void tearDown() {
//        ticketRepository.deleteAll();
//        ticketTypeRepository.deleteAll();
//        eventItemRepository.deleteAll();
//        memberRepository.deleteAll();
//    }
//
//    @Test
//    public void testOptimisticLock() throws InterruptedException {
//        runConcurrencyPurchase(TicketIssueStrategyType.OPTIMISTIC.getCode());
//        verifyResults();
//    }
//
//    @Test
//    public void testPessimisticLock() throws InterruptedException {
//        runConcurrencyPurchase(TicketIssueStrategyType.PESSIMISTIC.getCode());
//        verifyResults();
//    }
//
//    @Test
//    public void testRedisLock() throws InterruptedException {
//        runConcurrencyPurchase(TicketIssueStrategyType.REDIS.getCode());
//        verifyResults();
//    }
//
//    private void runConcurrencyPurchase(String strategyType) throws InterruptedException {
//        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
//        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
//
//        for (int i = 0; i < THREAD_COUNT; i++) {
//            executor.submit(() -> {
//                try {
//                    TicketPurchaseRequest request = new TicketPurchaseRequest();
//                    request.setTicketTypeId(ticketTypeId);
//                    request.setQuantity(1);
//
//                    ticketService.purchaseTicket(strategyType, request, memberId);
//                } catch (Exception e) {
//                    e.printStackTrace();  // 실패한 요청 로그 확인
//                    System.out.println(e.getMessage());
//                    failCount.incrementAndGet();
//
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//        executor.shutdown();
//    }
//
//    private void verifyResults() {
//        long issuedCount = ticketTypeRepository.findById(ticketTypeId).orElseThrow().getIssuedQuantity();
//        long ticketCount = ticketRepository.count();
//
//        assertAll("동시성 티켓 발급 정합성 검증",
//                () -> assertThat(ticketCount)
//                        .as("티켓 테이블 발급 수")
//                        .isEqualTo(THREAD_COUNT),
//                () -> assertThat(issuedCount)
//                        .as("issuedQuantity 증가량")
//                        .isEqualTo(THREAD_COUNT)
//        );
//    }
//
//
//}