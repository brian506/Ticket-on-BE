package com.ticketon.ticketon.domain.ticket.repository;

import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "3000") // 타임아웃 3초 지정
    })
    @Query("SELECT t FROM TicketType t WHERE t.id = :id")
    Optional<TicketType> findByIdForUpdate(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE TicketType t SET t.issuedQuantity = t.issuedQuantity + 1 "+
            "WHERE t.id = :id AND t.maxQuantity > t.issuedQuantity")
    int decreaseTicketAtomically(@Param("id") Long id);
}
