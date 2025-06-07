package com.ticketon.ticketon.domain.ticket.entity;

import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketTypeStatus;
import com.ticketon.ticketon.exception.custom.ExceededTicketQuantityException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "ticket_types")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_type_id", nullable = false)
    private Long id;

    // 공연 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventItem eventItem;

    // 티켓 종류 이름   ex) 그린석, 스탠딩석
    @Column(name = "ticket_type_name", nullable = false)
    private String name;

    // 해당 티켓에 대한 설명
    @Column(name = "description")
    private String description;

    // 발급할 티켓 개수
    @Column(name = "max_quantity", nullable = false)
    private Long maxQuantity;

    // 현재까지 발급된 티켓 개수
    // !! 동시성 문제가 발생할 수 있는 칼럼. 락처리를 잘 해야 할 듯 함 !!
    @Column(name = "issued_quantity", nullable = false)
    private Long issuedQuantity;

    // 해당 티켓 가격
    @Column(name = "price", nullable = false)
    private Long price;

    // 티켓 상태
    @Column(name = "status", nullable = false)
    private TicketTypeStatus status;

    public void issueTicket() {
        if (this.issuedQuantity >= this.maxQuantity) {
            // 남은 티켓보다 더 많은 티켓 발급 요청시 예외처리
            throw new ExceededTicketQuantityException(this);
        }
        this.issuedQuantity++;
    }

}
