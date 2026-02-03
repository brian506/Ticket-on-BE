package com.ticketon.ticketon.domain.ticket.service;

import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockRunner implements ApplicationRunner {

    private final TicketTypeRepository ticketTypeRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<TicketType> ticketTypes = ticketTypeRepository.findAll();

        for(TicketType type : ticketTypes) {
            String key = "issued_quantity:" + type.getId();
            redisTemplate.opsForValue().set(key,String.valueOf(type.getMaxQuantity()));
            log.info("[redis에 티켓 수량 적재 :  {} ]", type.getMaxQuantity());
        }

    }
}
