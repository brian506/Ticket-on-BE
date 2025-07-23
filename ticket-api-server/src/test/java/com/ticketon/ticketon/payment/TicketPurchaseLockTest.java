package com.ticketon.ticketon.payment;

import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import com.ticketon.ticketon.domain.eventitem.repository.EventItemRepository;
import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.payment.service.PaymentService;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketTypeStatus;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.domain.ticket.service.strategy.PessimisticLockTicketIssueService;

import jakarta.transaction.Transactional;
import jdk.jfr.Event;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest // 전체 컨텍스트 로딩
@Transactional
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3306/ticket_on?serverTimezone=Asia/Seoul",
        "spring.datasource.username=root",
        "spring.datasource.password=wind6298",
        "toss.client-key=test_ck_pP2YxJ4K871KbDe5qoQWVRGZwXLO",
        "waiting.api.enter-url="// 테스트 코드에서는 환경변수를 미리 설정
})
public class TicketPurchaseLockTest {

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EventItemRepository eventItemRepository;

    @Autowired
    private PaymentService paymentService;

//    @Autowired
//    private RedisLockTicketIssueService redisLockService;

    private static final int CONCURRENT_USERS = 100; // 사용자수
    private static final Long TICKET_STOCK = 50L; // 티켓 재고
    private Long ticketTypeId;
    private List<Long> ids; // 유저Id 저장 리스트

    @BeforeEach
    void setUp(){
        paymentRepository.deleteAllInBatch();
        ticketRepository.deleteAllInBatch();
        ticketTypeRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        eventItemRepository.deleteAllInBatch();

        ids = setupTestData();
        ticketTypeId = setupTicketType();

    }

    @Test
    @DisplayName("100명이 동시에 티켓 구매 요청 시 비관적 락 정상 수행") // 티켓 수량에 처리에 대한 비관락
    void concurrentTest_PessimisticLock() throws InterruptedException {
        //given
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS); // 스레드풀을 만들어줌
        CountDownLatch startLatch = new CountDownLatch(1); // 쓰레드를 동시에 시작하기 위함
        CountDownLatch finishLatch = new CountDownLatch(CONCURRENT_USERS); // 각 스레드 작업이 끝날때마다 0 이 될때까지 finish.countDown() 호출

        for(int i = 0; i < CONCURRENT_USERS;i++){
            final int index = i;
            // 별도의 스레드에서 실행
            executorService.submit(() -> {
                try{
                    startLatch.await(); // 시작신호가 오기전까지 모든 요청 대기
                    Long memberId = ids.get(index);
                    PaymentMessage message = PaymentMessage.builder()
                            .ticketTypeId(ticketTypeId)
                            .memberId(memberId)
                            .amount(10000)
                            .paymentKey("payment-key")
                            .orderId("orderId" + memberId)
                            .approvedAt(LocalDateTime.now())
                            .requestedAt(LocalDateTime.now())
                            .build();

                    paymentService.saveTicketAndPayment(message);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    finishLatch.countDown(); // 작업 완료
                }
            });
        }
        //when
        startLatch.countDown(); // 스레드 시작
        finishLatch.await(20, TimeUnit.SECONDS); // 종료 대기
        executorService.shutdown(); // 종료

        //then
        TicketType result = ticketTypeRepository.findById(ticketTypeId).orElseThrow();
        List<Ticket> tickets = ticketRepository.findAll();

        Assertions.assertEquals(result.getIssuedQuantity(),TICKET_STOCK);
        Assertions.assertEquals(tickets.size(),TICKET_STOCK);

    }
    @Test
    @DisplayName("100명이 동시에 티켓 구매 요청 시 레디스 락 수행")
    void concurrentTest_redisLock() throws Exception {
        //given
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(CONCURRENT_USERS);

//        for(int i = 0; i < CONCURRENT_USERS; i++){
//            final in
//        }

        //when

        //then

    }


    private List<Long> setupTestData() {
        // 회원 데이터 생성
        // generateValue 로 생성된 id 값으로 저장해야됨
        List<Long> ids = new ArrayList<>();
        for (int i = 1; i <= CONCURRENT_USERS; i++) {
            Member member = Member.builder()
                    .email("user" + i + "@test.com")
                    .password("password" + i)
                    .build();
            ids.add(memberRepository.save(member).getId());

        }
        return ids;
    }

    private Long setupTicketType() {
        EventItem eventItem = setUpEventItem();
        TicketType ticketType = TicketType.builder()
                .eventItem(eventItem)
                .name("테스트 티켓")
                .price(10000)
                .maxQuantity( TICKET_STOCK)
                .issuedQuantity(0L)
                .status(TicketTypeStatus.READY)
                .build();
        ticketTypeRepository.save(ticketType);
        return ticketType.getId();
    }

    private EventItem setUpEventItem(){
        EventItem eventItem = EventItem.builder()
                .title("싸이의 흠뻑쇼")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .build();
        return eventItemRepository.save(eventItem);
    }



}
