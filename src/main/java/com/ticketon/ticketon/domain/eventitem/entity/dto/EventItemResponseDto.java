package com.ticketon.ticketon.domain.eventitem.entity.dto;

import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketTypeResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class EventItemResponseDto {

    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TicketTypeResponseDto> ticketTypes;

    public static EventItemResponseDto from(EventItem eventItem) {
        return EventItemResponseDto.builder()
                .id(eventItem.getId())
                .title(eventItem.getTitle())
                .startDate(eventItem.getStartDate())
                .endDate(eventItem.getEndDate())
                .ticketTypes(eventItem.getTicketTypes() == null ?
                        Collections.emptyList() :
                        eventItem.getTicketTypes().stream()
                                .map(TicketTypeResponseDto::from)
                                .collect(Collectors.toList()))
                .build();
    }
}
