package com.ticketon.ticketon.domain.ticket.entity;

import com.ticket.exception.custom.TicketAlreadyCancelledException;
import com.ticketon.ticketon.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Table(name = "tickets")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus ticketStatus;

    @Column(name = "price",nullable = false)
    private Integer price;

    @Column(name ="order_id",nullable = false)
    private String orderId;

    @Column(name = "expired_at",nullable = false)
    private LocalDateTime expiredAt;

    public Long getTicketTypeId() {
        return ticketType.getId();
    }

    public Long getMemberId() {
        return member.getId();
    }

    public void cancel() {
        validateCancelable();
        this.ticketStatus = TicketStatus.CANCELLED;
    }

    private void validateCancelable() {
        if (this.ticketStatus.equals(TicketStatus.CANCELLED)) {
            throw new TicketAlreadyCancelledException();
        }
    }

    public static Ticket createNormalTicket(TicketType ticketType, Member member,String orderId) {
        return Ticket.builder().
                ticketType(ticketType).
                member(member).
                price(ticketType.getPrice()).
                orderId(orderId).
                ticketStatus(TicketStatus.SOLD_OUT).
                build();
    }

    public static Ticket createTicket(TicketType ticketType,Member member,String orderId){
        return Ticket.builder()
                .member(member)
                .ticketType(ticketType)
                .price(ticketType.getPrice())
                .orderId(orderId)
                .ticketStatus(TicketStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

    }
}
