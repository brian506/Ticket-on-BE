package com.ticketon.ticketon.domain.payment.entity;

import com.ticketon.ticketon.domain.payment.dto.PaymentCancelResponse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Setter
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "requested_at",nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_at",nullable = true)
    private LocalDateTime approvedAt;

    @Setter
    @Column(name = "canceled_at",nullable = true)
    private LocalDateTime canceledAt;




}
