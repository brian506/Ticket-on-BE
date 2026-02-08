package com.ticketon.ticketon.domain.ticket.repository.custom;

import com.querydsl.core.types.Projections;
import com.ticketon.ticketon.domain.ticket.dto.ExpiredTicket;

import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import com.ticketon.ticketon.domain.ticket.entity.TicketStatus;
import com.ticketon.ticketon.utils.QuerydslRepositorySupport;

import static com.ticketon.ticketon.domain.ticket.entity.QTicket.ticket;

import java.time.LocalDateTime;
import java.util.List;

public class TicketCustomRepositoryImpl extends QuerydslRepositorySupport implements TicketCustomRepository{

    protected TicketCustomRepositoryImpl() {
        super(Ticket.class);
    }

    @Override
    public Long updateTicketStatus(Long ticketId) {
       return update(ticket)
                .set(ticket.ticketStatus, TicketStatus.PAID)
                .where(ticket.id.eq(ticketId),
                        ticket.ticketStatus.eq(TicketStatus.PENDING),
                        ticket.expiredAt.gt(LocalDateTime.now()))
                        .execute();
    }

    @Override
    public List<ExpiredTicket> findExpiredTickets(LocalDateTime now) {
        return select(Projections.constructor(ExpiredTicket.class,
                ticket.id,
                ticket.ticketType.id,
                ticket.orderId,
                ticket.count()
        ))
                .from(ticket)
                .where(ticket.ticketStatus.eq(TicketStatus.PENDING),
                        ticket.expiredAt.lt(now))
                .groupBy(ticket.ticketType.id)
                .fetch();

    }

    @Override
    public void bulkUpdateStatusToExpiredByIds(List<Long> ticketIds, LocalDateTime now) {
        update(ticket)
                .set(ticket.ticketStatus, TicketStatus.CANCELLED)
                .where(ticket.id.in(ticketIds),
                        ticket.ticketStatus.eq(TicketStatus.PENDING),
                        ticket.expiredAt.before(now)
                )
                .execute();

        getEntityManager().clear();
    }


}
