package com.ticketon.ticketon.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class CancelDetail {
    @JsonProperty("canceledAt")
    private OffsetDateTime canceledAt;

    @JsonProperty("cancelReason")
    private String cancelReason;

    @JsonProperty("cancelAmount")
    private int cancelAmount;
}
