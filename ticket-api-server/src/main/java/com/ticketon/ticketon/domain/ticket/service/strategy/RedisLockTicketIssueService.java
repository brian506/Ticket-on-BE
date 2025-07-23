package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.ticket.dto.TicketRequest;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.utils.OptionalUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service(TicketIssueStrategyType.REDIS_STRATEGY_NAME)
@Qualifier(TicketIssueStrategyType.REDIS_STRATEGY_NAME)
@RequiredArgsConstructor
public class RedisLockTicketIssueService implements TicketIssueStrategy{

    private final RedissonClient redissonClient;
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final MemberRepository memberRepository;

    //todo 레디스 유틸 클래스로 대체

    // 멀티 인스턴스 환경에서의 동시성 제어를 위한 레디스락
    @Transactional
    @Override
    public TicketType purchaseTicket(PaymentMessage message, Long memberId) {
        String lockName = "ticketTypeLock" + message.getTicketTypeId();
        RLock lock = redissonClient.getLock(lockName);
        Long ticketTypeId = message.getTicketTypeId();

        try {
            // waitTime : 락을 획득하기 전까지의 대기 시간. 시간이 지나면 false 를 반환하고 대기 종료
            // leaseTime : 락을 획득하고 나서의 시간. 시간이 지나면 락 해제
            boolean available = lock.tryLock(5, 3, TimeUnit.SECONDS);
            // 레디스 락 획득 이후 비관락 적용
            if (!available) {
                throw new RuntimeException("락 획득 실패");
            }
            // Pessimistic Lock 함께 사용
            TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findByIdForUpdate(ticketTypeId), "티켓 타입 조회 실패 ticket_id=" + ticketTypeId);
            return ticketType;

        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock(); // 락 해제
            }
        }
    }
}
