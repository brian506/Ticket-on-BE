package com.ticketon.ticketon.domain.payment.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentCancelResponse {

    @JsonProperty("orderId")
    private String ticketId;

    private LocalDateTime requestedAt;

    private LocalDateTime cancelledAt;

}
