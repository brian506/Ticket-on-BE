package com.ticketon.ticketon.domain.eventitem.dto;

import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import com.ticketon.ticketon.domain.ticket.entity.dto.TicketTypeResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class EventItemResponse {

    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TicketTypeResponse> ticketTypes;

    public static EventItemResponse from(EventItem eventItem) {
        return EventItemResponse.builder()
                .id(eventItem.getId())
                .title(eventItem.getTitle())
                .startDate(eventItem.getStartDate())
                .endDate(eventItem.getEndDate())
                .ticketTypes(eventItem.getTicketTypes() == null ?
                        Collections.emptyList() :
                        eventItem.getTicketTypes().stream()
                                .map(TicketTypeResponse::from)
                                .collect(Collectors.toList()))
                .build();
    }
}
