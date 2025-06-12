package com.ticketon.ticketon.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PaymentCancelRequest {

    private String paymentKey;

    private String cancelReason = "단순변심";

    private int cancelAmount;


}
