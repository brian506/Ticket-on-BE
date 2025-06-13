package com.ticketon.ticketon.domain.payment.repository;

import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 결제 테이블에서 예약 테이블 pk 값 조회
    Optional<Payment> findByTicketId(Long ticketId);

    Optional<Payment> findByPaymentKey(String paymentKey);

    Optional<Payment> findByMemberId(Long memberId);
}
