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

    @Setter
    @Column(name = "ticket_id",nullable = true)
    private Long ticketId;

    @Column(name = "ticket_type_id")
    private Long ticketTypeId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "orderId",nullable = false)
    private String orderId;

    @Setter
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "requested_at",nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_at",nullable = true)
    private LocalDateTime approvedAt;

    @Setter
    @Column(name = "canceled_at",nullable = true)
    private LocalDateTime canceledAt;

    public void cancelPayment(final LocalDateTime canceledAt){
        this.paymentStatus = PaymentStatus.CANCELED;
        this.canceledAt = canceledAt;
    }

}