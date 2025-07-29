package com.ticketon.ticketon.domain.payment.comsumer;

import com.ticketon.ticketon.domain.payment.dto.PaymentMessage;
import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.repository.PaymentRepository;
import com.ticketon.ticketon.domain.payment.service.PaymentService;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.domain.ticket.service.TicketService;
import com.ticketon.ticketon.domain.ticket.service.strategy.RedisLockTicketIssueService;
import com.ticketon.ticketon.utils.OptionalUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;


@Profile("!test")
@Slf4j
@Service
@RequiredArgsConstructor
@KafkaListener(
        topics = "${kafka.topic-config.payment.name}",
        groupId = "${kafka.consumer.payment-group.group-id}",
        containerFactory = "paymentKafkaBatchListenerContainerFactory")
public class PaymentConsumer {

    private final PaymentService paymentService;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketService ticketService;
    private final RedisLockTicketIssueService redisLock;

    // 예약,결제 정보 저장
    @KafkaHandler
    public void consumePayment(List<PaymentMessage> messages, Acknowledgment ack) {
        if (messages.isEmpty()) {
            return; // Kafka ack 안 해주면 retry 발생
        }
        Long ticketTypeId = messages.get(0).getTicketTypeId();
        String lockName = "ticket:lock" + ticketTypeId;

        try {
           redisLock.redisLockOnMessage(lockName, () ->{
               entireTransaction(messages,ticketTypeId);
               return null;
           });

            // 위의 로직이 다 끝났을 때 호출
            ack.acknowledge();

        }catch (Exception e){
            log.error("payment message invalid : {}", e);
        }

    }

    @Transactional
    public void entireTransaction(List<PaymentMessage> messages,Long ticketTypeId){

        TicketType ticketType = OptionalUtil.getOrElseThrow(ticketTypeRepository.findById(ticketTypeId),"존재하지 않는 ticketTypeId");

        List<Ticket> successTickets = ticketService.createTicketInfoInBatch(ticketType,messages);

        List<Ticket> savedTickets = ticketService.saveAll(successTickets);

        if(!savedTickets.isEmpty()){
            paymentService.savePaymentsByTickets(savedTickets,messages);
        }

    }
}

