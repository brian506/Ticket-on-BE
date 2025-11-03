package com.ticketon.ticketon.domain.ticket.entity;

import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketTypeStatus;
import com.ticket.exception.custom.ExceededTicketQuantityException;
import com.ticket.exception.custom.InvalidTicketCancellationException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    @Column(name = "issued_quantity", nullable = false)
    private Long issuedQuantity;

//    // 발급 가능한 티켓 개수
//    @Column(name = "available_quantity",nullable = false)
//    private Long availableQuantity;

    // 해당 티켓 가격
    @Column(name = "price", nullable = false)
    private Integer price;

    // 티켓 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketTypeStatus status;

    public void increaseIssuedQuantity() {
        validateCanIssueTicket();
        this.issuedQuantity++;
    }
    public Long getAvailableQuantity(){
        return this.maxQuantity - this.issuedQuantity;
    }

    public void decreaseTicketQuantity() {
        validateCanCancelTicket();
        this.issuedQuantity--;
    }

    public void validateCanIssueTicket() {
        log.info("발행 수량 확인: issuedQuantity={}, maxQuantity={}", issuedQuantity, maxQuantity);
        if (this.issuedQuantity >= this.maxQuantity) {
            throw new ExceededTicketQuantityException(this.getName(), this.getPrice());
        }
    }

    private void validateCanCancelTicket() {
        if (this.issuedQuantity < 1) {
            throw new InvalidTicketCancellationException(this.getName(), this.getPrice());
        }
    }

    public boolean decreaseStock(){
        if (this.maxQuantity - this.issuedQuantity > 0){
            return true;
        }
        return false;
    }

}