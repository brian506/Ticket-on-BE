package com.ticketon.ticketon.domain.ticket.entity;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.exception.custom.TicketAlreadyCancelledException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    public void cancel(){
        if (this.ticketStatus.equals(TicketStatus.CANCELLED)) {
            throw new TicketAlreadyCancelledException();
        }
        this.ticketStatus = TicketStatus.CANCELLED;
    }

    public static Ticket createNormalTicket(TicketType ticketType, Member member) {
        return Ticket.builder().
                ticketType(ticketType).
                member(member).
                ticketStatus(TicketStatus.SOLD_OUT).
                build();
    }
}
