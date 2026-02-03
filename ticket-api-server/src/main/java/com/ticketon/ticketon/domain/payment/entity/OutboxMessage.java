package com.ticketon.ticketon.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "outbox_message")
public class OutboxMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_message_id")
    private Long id;

    @Column(name = "topic",nullable = false)
    private String topic;

    // 모든 dto 가 JSON 으로 저장
    @Column(columnDefinition = "TEXT")
    private String payload;

    public static OutboxMessage toEntityFromTicket(String payload){
        return OutboxMessage.builder()
                .topic("payment-confirm")
                .payload(payload)
                .build();
    }
}
