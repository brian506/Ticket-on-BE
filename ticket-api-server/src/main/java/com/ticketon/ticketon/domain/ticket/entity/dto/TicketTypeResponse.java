package com.ticketon.ticketon.domain.ticket.entity.dto;


import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketTypeResponse {

    private Long id;
    private String name;
    private String description;
    private Long maxIssueQuantity;
    private Long currentIssuedCount;
    private Integer price;
    private TicketTypeStatus ticketTypeStatus;

    public static TicketTypeResponse from(TicketType ticketType) {
        return TicketTypeResponse.builder()
                .id(ticketType.getId())
                .name(ticketType.getName())
                .description(ticketType.getDescription())
                .maxIssueQuantity(ticketType.getMaxQuantity())
                .currentIssuedCount(ticketType.getIssuedQuantity())
                .price(ticketType.getPrice())
                .ticketTypeStatus(ticketType.getStatus())
                .build();
    }
}
