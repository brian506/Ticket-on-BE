package com.ticketon.ticketon.domain.ticket.repository.custom;

import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.utils.QuerydslRepositorySupport;
import jakarta.persistence.LockModeType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ticketon.ticketon.domain.ticket.entity.QTicketType.ticketType;

public class TicketTypeCustomRepositoryImpl extends QuerydslRepositorySupport implements TicketTypeCustomRepository {
    protected TicketTypeCustomRepositoryImpl() {
        super(TicketType.class);
    }

    // 비관락
    @Override
    public Optional<TicketType> findByIdForUpdate(Long ticketTypeId) {
        return Optional.ofNullable(
                selectFrom(ticketType)
                        .where(ticketType.id.eq(ticketTypeId))
                        .setLockMode(LockModeType.WRITE)
                        .setHint("javax.persistence.lock.timeout", 3000)
                        .fetchOne()
        );
    }

    @Override
    public Long increaseTicketAtomically(Long ticketTypeId) {
        long updatedRows = update(ticketType)
                .set(ticketType.issuedQuantity, ticketType.issuedQuantity.add(1))
                .where(ticketType.id.eq(ticketTypeId),
                        ticketType.maxQuantity.gt(ticketType.issuedQuantity))
                .execute();

        getEntityManager().clear();
        return updatedRows;
    }

    @Override
    public void decreaseIssuedTickets(Long ticketTypeId, Long count) {
        update(ticketType)
                .set(ticketType.issuedQuantity, ticketType.issuedQuantity.subtract(count))
                .where(ticketType.id.eq(ticketTypeId))
                .execute();
        getEntityManager().clear();
    }
}
