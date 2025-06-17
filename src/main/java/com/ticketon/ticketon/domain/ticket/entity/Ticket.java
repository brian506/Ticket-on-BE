package com.ticketon.ticketon.domain.ticket.entity;

import com.ticketon.ticketon.domain.member.entity.Member;
import com.ticketon.ticketon.exception.custom.TicketAlreadyCancelledException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    // 공연 날짜 및 시간
    @Column(name = "event_date_time", nullable = false)
    private LocalDateTime eventDateTime;

    // 좌석 정보 (예: A열 12번)
    @Column(name = "seat_info", nullable = false)
    private String seatInfo;

    // 가격
    @Column(name = "price", nullable = false)
    private Integer price;

    // 티켓 상태 (예: 예약됨, 사용됨, 취소됨 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status;

    // 티켓 생성 시간
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 티켓 마지막 수정 시간
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public static Ticket createNormalTicket(TicketType ticketType, Member member) {
        return Ticket.builder().
                ticketType(ticketType).
                member(member).
                ticketStatus(TicketStatus.SOLD_OUT).
                build();
    }
}
