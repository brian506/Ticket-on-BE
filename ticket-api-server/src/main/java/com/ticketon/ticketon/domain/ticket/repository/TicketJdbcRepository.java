package com.ticketon.ticketon.domain.ticket.repository;

import com.ticketon.ticketon.domain.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * 티켓 대량 저장 전용 리포지토리.
 * <p>
 * Ticket 은 IDENTITY 전략이라 Hibernate 가 persist 시점마다 INSERT 를 즉시 실행해 JDBC 배치가 비활성화된다.
 * 저장 후 id 가 필요 없는 대량 저장 경로만 JPA 를 우회해 JDBC 배치로 처리하고,
 * rewriteBatchedStatements=true 로 MySQL 드라이버가 multi-row INSERT 로 재작성하게 한다.
 */
@Repository
@RequiredArgsConstructor
public class TicketJdbcRepository {

    private static final String INSERT_SQL =
            "INSERT INTO tickets (ticket_type_id, member_id, status, price, order_id, expired_at) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<Ticket> tickets) {
        jdbcTemplate.batchUpdate(INSERT_SQL, tickets, tickets.size(), (ps, ticket) -> {
            ps.setLong(1, ticket.getTicketTypeId());
            ps.setLong(2, ticket.getMemberId());
            ps.setString(3, ticket.getTicketStatus().name());
            ps.setInt(4, ticket.getPrice());
            ps.setString(5, ticket.getOrderId());
            ps.setTimestamp(6, Timestamp.valueOf(ticket.getExpiredAt()));
        });
    }
}
