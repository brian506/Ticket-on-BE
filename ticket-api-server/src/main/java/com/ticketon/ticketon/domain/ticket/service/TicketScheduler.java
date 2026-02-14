package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.ticket.dto.ExpiredTicket;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.repository.TicketRepository;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.ListUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketScheduler {

    private final StringRedisTemplate redisTemplate;
    private final TicketService ticketService;


//    @Scheduled(fixedRate = 60000)
    public void removePendingTickets() {

        LocalDateTime now = LocalDateTime.now();
        List<ExpiredTicket> tickets = ticketService.findExpiredTickets(now);
        if(tickets.isEmpty()) return;

        List<Long> cancelTicket = new ArrayList<>();

        for(ExpiredTicket ticket : tickets) {
            // 결제 성공건은 재고 감소에서 pass
            boolean isPaid = redisTemplate.hasKey("payment_success:" + ticket.orderId());
            if(isPaid) continue;

            String stockKey = "issued_quantity:" + ticket.ticketTypeId();
            redisTemplate.opsForValue().increment(stockKey, 1);
            cancelTicket.add(ticket.ticketId());
        }
        ticketService.updateExpiredTickets(cancelTicket,now);

        log.info("결제 미완료건 재고 복구 완료 : {}건,", tickets.size());
    }
}
