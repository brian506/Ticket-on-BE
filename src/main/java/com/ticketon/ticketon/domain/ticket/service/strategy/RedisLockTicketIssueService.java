package com.ticketon.ticketon.domain.ticket.service.strategy;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.domain.member.repository.MemberRepository;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.exception.custom.DataNotFoundException;
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
    @Transactional
    @Override
    public void purchaseTicket(TicketPurchaseRequest request, Long memberId) {
        String lockName = "ticketTypeLock" + request.getTicketTypeId();
        RLock lock = redissonClient.getLock(lockName);

        try {
            if (lock.tryLock(5, 3, TimeUnit.SECONDS)) {
                Long ticketTypeId = request.getTicketTypeId();
                // Pessimistic Lock 함께 사용
                TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findByIdForUpdate(ticketTypeId), "티켓 타입 조회 실패 ticket_id=" + ticketTypeId);

                // 쿼리 날리지 않고 프록시로 조회
                Member member = memberRepository.getReferenceById(memberId);

                ticketType.increaseIssuedQuantity();

                Ticket ticket = Ticket.createNormalTicket(ticketType, member);
                ticketRepository.save(ticket);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
