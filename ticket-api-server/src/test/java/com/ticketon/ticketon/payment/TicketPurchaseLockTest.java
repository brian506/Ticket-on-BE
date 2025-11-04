//package com.ticketon.ticketon.payment;
//
//import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
//import com.ticketon.ticketon.domain.eventitem.entity.EventItemStatus;
//import com.ticketon.ticketon.domain.eventitem.repository.EventItemRepository;
//import com.ticketon.ticketon.domain.member.entity.Member;
//import com.ticketon.ticketon.domain.member.repository.MemberRepository;
//import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
//import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
//import com.ticketon.ticketon.domain.payment.service.PaymentService;
//import com.ticketon.ticketon.domain.ticket.entity.Ticket;
//import com.ticketon.ticketon.domain.ticket.entity.TicketType;
//import com.ticketon.ticketon.domain.ticket.entity.dto.TicketTypeStatus;
//import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
//import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class TicketPurchaseLockTest {
//
//    @Autowired
//    private TicketTypeRepository ticketTypeRepository;
//
//    @Autowired
//    private TicketRepository ticketRepository;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    @Autowired
//    private EventItemRepository eventItemRepository;
//
//    @Autowired
//    private PaymentService paymentService;
//
//    private static final int CONCURRENT_USERS = 100; // 사용자수
//    private static final Long TICKET_STOCK = 100L; // 티켓 재고
//    private Long ticketTypeId;
//    private List<Long> ids; // 유저Id 저장 리스트
//
//    @BeforeEach
//    void setUp(){
//        paymentRepository.deleteAllInBatch();
//        ticketRepository.deleteAllInBatch();
//        ticketTypeRepository.deleteAllInBatch();
//        memberRepository.deleteAllInBatch();
//        eventItemRepository.deleteAllInBatch();
//
//        ids = setupTestData();
//        ticketTypeId = setupTicketType();
//    }
////    @AfterEach
////    void cleanUp(){
////        paymentRepository.deleteAllInBatch();
////        ticketRepository.deleteAllInBatch();
////        ticketTypeRepository.deleteAllInBatch();
////        memberRepository.deleteAllInBatch();
////        eventItemRepository.deleteAllInBatch();
////    }
//
//    @Test
//    @DisplayName("100명이 동시에 티켓 구매 요청 시 비관적 락 정상 수행") // 티켓 수량에 처리에 대한 비관락
//    void concurrentTest_PessimisticLock() throws InterruptedException {
//        //given
//        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
//        CountDownLatch startLatch = new CountDownLatch(1);
//        CountDownLatch finishLatch = new CountDownLatch(CONCURRENT_USERS);
//
//        for(int i = 0; i < CONCURRENT_USERS;i++){
//            final int index = i;
//            executorService.submit(() -> {
//                try{
//                    startLatch.await();
//                    Long memberId = ids.get(index);
//                    PaymentMessage message = setUpPaymentMessage(memberId);
//                    paymentService.saveTicketAndPayment(message);
//                }catch (InterruptedException e){
//                    e.getStackTrace();
//                }finally {
//                    finishLatch.countDown();
//                }
//            });
//        }
//        //when
//        startLatch.countDown();
//        finishLatch.await(20, TimeUnit.SECONDS);
//        executorService.shutdown();
//
//        //then
//        TicketType result = ticketTypeRepository.findById(ticketTypeId).orElseThrow();
//        List<Ticket> tickets = ticketRepository.findAll();
//
//        Assertions.assertEquals(result.getIssuedQuantity(),TICKET_STOCK);
//        Assertions.assertEquals(tickets.size(),TICKET_STOCK);
//    }
//
//    @Test
//    @DisplayName("100명이 동시에 티켓 구매 요청 시 레디스 락 수행")
//    void concurrentTest_redisLock() throws Exception {
//        //given
//        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
//        CountDownLatch startLatch = new CountDownLatch(1);
//        CountDownLatch finishLatch = new CountDownLatch(CONCURRENT_USERS);
//
//        for(int i = 0; i < CONCURRENT_USERS; i++){
//            final int index = i;
//            executorService.submit(() -> {
//                try {
//                    startLatch.await();
//                    Long memberId = ids.get(index);
//                    PaymentMessage message = setUpPaymentMessage(memberId);
//                    paymentService.saveTicketAndPayment(message);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }finally {
//                    finishLatch.countDown();
//                }
//            });
//        }
//        //when
//        startLatch.countDown();
//        finishLatch.await(20,TimeUnit.SECONDS);
//        executorService.shutdown();
//
//        //then
//        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId).orElseThrow();
//        List<Ticket> tickets = ticketRepository.findAll();
//
//        Assertions.assertEquals(ticketType.getIssuedQuantity(),TICKET_STOCK);
//        Assertions.assertEquals(tickets.size(),TICKET_STOCK);
//
//    }
//
//
//    private List<Long> setupTestData() {
//        List<Long> ids = new ArrayList<>();
//        for (int i = 1; i <= CONCURRENT_USERS; i++) {
//            Member member = Member.builder()
//                    .email("user" + i + "@test.com")
//                    .password("password" + i)
//                    .build();
//            ids.add(memberRepository.save(member).getId());
//
//        }
//        return ids;
//    }
//
//    private Long setupTicketType() {
//        EventItem eventItem = setUpEventItem();
//        TicketType ticketType = TicketType.builder()
//                .eventItem(eventItem)
//                .name("테스트 티켓")
//                .price(1000)
//                .maxQuantity( TICKET_STOCK)
//                .issuedQuantity(0L)
//                .status(TicketTypeStatus.READY)
//                .build();
//        ticketTypeRepository.save(ticketType);
//        return ticketType.getId();
//    }
//
//    private EventItem setUpEventItem(){
//        EventItem eventItem = EventItem.builder()
//                .title("싸이의 흠뻑쇼")
//                .startDate(LocalDate.now())
//                .endDate(LocalDate.now())
//                .eventItemStatus(EventItemStatus.OPEN)
//                .build();
//        return eventItemRepository.save(eventItem);
//    }
//
//    private PaymentMessage setUpPaymentMessage(Long memberId){
//         PaymentMessage paymentMessage = PaymentMessage.builder()
//                 .ticketTypeId(ticketTypeId)
//                 .memberId(memberId)
//                 .amount(1000)
//                 .paymentKey("payment-key")
//                 .orderId("orderId" + memberId)
//                 .approvedAt(LocalDateTime.now())
//                 .requestedAt(LocalDateTime.now())
//                 .build();
//         return paymentMessage;
//    }
//
//
//
//}
