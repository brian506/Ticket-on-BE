package com.ticketon.ticketon.domain.payment.entity;

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

    @Column(name = "user_id", nullable = false)
    private Long userId;

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

    @Column(name = "cancelled_at",nullable = true)
    private LocalDateTime cancelledAt;


}
