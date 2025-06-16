package com.ticketon.ticketon.domain.eventitem.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EventItemCreateRequest {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
}
