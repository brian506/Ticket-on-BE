package com.ticketon.ticketon.domain.payment.repository;

import com.ticketon.ticketon.domain.payment.entity.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxMessage,Long> {
}
