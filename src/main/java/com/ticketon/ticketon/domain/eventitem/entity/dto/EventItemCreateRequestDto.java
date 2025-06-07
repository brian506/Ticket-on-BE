package com.ticketon.ticketon.domain.eventitem.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EventItemCreateRequestDto {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
}
