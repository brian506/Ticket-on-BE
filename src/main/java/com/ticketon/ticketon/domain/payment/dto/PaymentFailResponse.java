package com.ticketon.ticketon.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentFailResponse {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

}
