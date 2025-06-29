package com.ticketon.ticketon.domain.payment.dto;

import com.ticketon.ticketon.domain.payment.entity.Payment;
import com.ticketon.ticketon.domain.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentConfirmRequest {
    private Long memberId;
    private String paymentKey;
    private String ticketId;
    private int amount;

    public TossConfirmRequest toTossConfirmRequest() {
        return new TossConfirmRequest(
                this.paymentKey,
                this.getTicketId(),
                this.amount);
    }


}
