package com.ticketon.ticketon.domain.ticket.entity.dto;


import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketTypeResponseDto {

    private Long id;
    private String name;
    private String description;
    private Long maxIssueQuantity;
    private Long currentIssuedCount;
    private Long price;
    private TicketTypeStatus ticketTypeStatus;

    public static TicketTypeResponseDto from(TicketType ticketType) {
        return TicketTypeResponseDto.builder()
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
