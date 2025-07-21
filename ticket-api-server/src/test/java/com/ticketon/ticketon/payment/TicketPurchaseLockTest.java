package com.ticketon.ticketon.payment;

import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import com.ticketon.ticketon.domain.eventitem.repository.EventItemRepository;
import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.payment.service.PaymentService;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketTypeStatus;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.domain.ticket.service.strategy.PessimisticLockTicketIssueService;
import jakarta.transaction.Transactional;
import jdk.jfr.Event;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

    @Autowired
    private PessimisticLockTicketIssueService ticketIssueService;

    private static final int CONCURRENT_USERS = 100;
    private static final int TICKET_STOCK = 10;

    @BeforeEach
    void setUp(){
        setupTestData();
    }

    @Test
    @DisplayName("100명이 동시에 티켓 구매 요청 시 비관적 락 정상 수행")
    void concurrentTest_PessimisticLock() throws InterruptedException {
        //given
        Long  ticketTypeId = 1L;
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS); // 스레드풀을 만들어줌
        CountDownLatch startLatch = new CountDownLatch(1); // 쓰레드를 동시에 시작하기 위함
        CountDownLatch finishLatch = new CountDownLatch(CONCURRENT_USERS);

        for(int i = 0; i < CONCURRENT_USERS;i++){
            final long memberId = i + 1;
            executorService.submit(() -> {
                try{
                    startLatch.await(); // 시작신호가 오기전까지 대기

                    PaymentMessage message = PaymentMessage.builder()
                            .ticketTypeId(ticketTypeId)
                            .memberId(memberId)
                            .amount(10000)
                            .paymentKey("payment-key")
                            .orderId("orderId" + memberId)
                            .requestedAt(LocalDateTime.now())
                            .requestedAt(LocalDateTime.now())
                            .build();

                    paymentService.saveTicketAndPayment(message);
                }catch (Exception ignored){
                }finally {
                    finishLatch.countDown(); // 작업 완료
                }
            });
        }
        //when
        startLatch.countDown(); // 스레드 시작
        finishLatch.await(20, TimeUnit.SECONDS);
        executorService.shutdown();
        //then

    }
    private void setupTestData() {
        // 회원 데이터 생성
        for (int i = 1; i <= CONCURRENT_USERS; i++) {
            Member member = Member.builder()
                    .id((long) i)
                    .email("user" + i + "@test.com")
                    .password("password" + i)
                    .build();
            memberRepository.save(member);
        }

        // 티켓 타입 생성 (재고 10개)
        setupTicketType(1L);
    }

    private void setupTicketType(Long id) {
        EventItem eventItem = setUpEventItem(id);
        TicketType ticketType = TicketType.builder()
                .id(id)
                .eventItem(eventItem)
                .name("테스트 티켓")
                .price(10000L)
                .maxQuantity((long) TICKET_STOCK)
                .issuedQuantity((long) TICKET_STOCK)
                .status(TicketTypeStatus.READY)
                .build();
        ticketTypeRepository.save(ticketType);
    }

    private EventItem setUpEventItem(Long id){
        EventItem eventItem = EventItem.builder()
                .id(1L)
                .title("싸이의 흠뻑쇼")
                .startDate(LocalDate.now())
                .endDate(LocalDate.MAX)
                .build();
        return eventItemRepository.save(eventItem);
    }

}
