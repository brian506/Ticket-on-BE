package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticket.exception.custom.ExceededTicketQuantityException;
import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import com.ticketon.ticketon.domain.eventitem.entity.EventItemStatus;
import com.ticketon.ticketon.domain.eventitem.repository.EventItemRepository;
import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketTypeStatus;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.integration.E2ETest;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;


public class TicketIssueStrategyTest extends E2ETest {

    @Autowired
    private TicketIssueStrategy ticketIssueStrategy;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventItemRepository eventItemRepository;

    @Autowired
    private EntityManager entityManager;

    private static int THREAD_COUNT = 32;
    private static final String orderId = new ULID().nextULID();

    @BeforeEach
    void setUp() {
        EventItem eventItem = EventItem.builder()
                .title("흠뻑쇼")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .eventItemStatus(EventItemStatus.OPEN)
                .build();
        eventItemRepository.save(eventItem);
        TicketType ticketType = TicketType.builder().
                eventItem(eventItem).
                name("티켓").
                issuedQuantity(0L).
                maxQuantity(1L).
                price(1000).
                status(TicketTypeStatus.READY)
                .build();
        ticketTypeRepository.save(ticketType);

        for (int i = 0; i < 32; i++) {
            Member member = Member.builder()
                    .email("user" + i + "@test.com")
                    .password("password")
                    .build();
            memberRepository.save(member);
        }
    }

    @Test
    @DisplayName("syncronized를 적용하여 32명이 하나의 티켓에 접근하여 티켓을 구입하려 해도 한명만 구입된다.")
    void synchronized_SUCCESS() throws Exception {
        //when
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        TicketType ticketType = ticketTypeRepository.findById(1L).orElseThrow();
        List<Member> members = memberRepository.findAll();

        for(int i = 0; i < THREAD_COUNT; i++) {
            Long memberId = members.get(i).getId();
            executorService.submit(() -> {
                try {
                    ticketIssueStrategy.purchaseTicketBySynchronize(ticketType.getId(), memberId, orderId);
                    successCount.incrementAndGet();
                }
                catch (Exception e) {
                    failCount.incrementAndGet();
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        entityManager.clear();
        //then
        TicketType updatedTicket = ticketTypeRepository.findById(ticketType.getId()).orElseThrow();

        assertThat(updatedTicket.getIssuedQuantity()).isEqualTo(1L);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(31);
    }


    @Test
    @DisplayName("낙관락을 적용하여 32명이 하나의 티켓에 접근하여 티켓을 구입하면 한명만 구입한다.")
    void optimisticLock_SUCCESS() throws InterruptedException{
        //when
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        TicketType ticketType = ticketTypeRepository.findById(1L).orElseThrow();
        List<Member> members = memberRepository.findAll();

        for(int i = 0; i < THREAD_COUNT; i++) {
            Long memberId = members.get(i).getId();
            executorService.submit(() -> {
                try {
                    ticketIssueStrategy.purchaseTicketByOptimisticLock(ticketType.getId(), memberId, orderId);
                    successCount.incrementAndGet();
                }
                catch (Exception e) {
                    failCount.incrementAndGet();
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        entityManager.clear();
        //then
        TicketType updatedTicket = ticketTypeRepository.findById(ticketType.getId()).orElseThrow();

        assertThat(updatedTicket.getIssuedQuantity()).isEqualTo(1L);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(31);
    }

    @Test
    @DisplayName("비관락을 이용하여 32명이 티켓을 구입하려고 해도 1개만 접근할 수 있다.")
    void pessimisticLock_SUCCESS() throws InterruptedException {
        //when
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        TicketType ticketType = ticketTypeRepository.findById(1L).orElseThrow();
        List<Member> members = memberRepository.findAll();

        for(int i = 0; i < THREAD_COUNT; i++) {
            Long memberId = members.get(i).getId();
            executorService.submit(() -> {
                try {
                    ticketIssueStrategy.purchaseTicketByPessimisticLock(ticketType.getId(), memberId, orderId);
                    successCount.incrementAndGet();
                }
                catch (Exception e) {
                    failCount.incrementAndGet();
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        entityManager.clear();
        //then
        TicketType updatedTicket = ticketTypeRepository.findById(ticketType.getId()).orElseThrow();

        assertThat(updatedTicket.getIssuedQuantity()).isEqualTo(1L);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(31);
    }

    @Test
    @DisplayName("원자적 연산으로 DB 내에서 바로 ")
    void atomic_SUCCESS() throws InterruptedException{
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        TicketType ticketType = ticketTypeRepository.findById(1L).orElseThrow();
        List<Member> members = memberRepository.findAll();

        for(int i = 0; i < THREAD_COUNT; i++) {
            Long memberId = members.get(i).getId();
            executorService.submit(() -> {
                try {
                    ticketIssueStrategy.purchaseTicketAtomic(ticketType.getId(), memberId, orderId);
                    successCount.incrementAndGet();
                }
                catch (ExceededTicketQuantityException e) {
                    failCount.incrementAndGet();
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        entityManager.clear();
        //then
        TicketType updatedTicket = ticketTypeRepository.findById(ticketType.getId()).orElseThrow();

        assertThat(updatedTicket.getIssuedQuantity()).isEqualTo(1L);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(31);

    }

}
