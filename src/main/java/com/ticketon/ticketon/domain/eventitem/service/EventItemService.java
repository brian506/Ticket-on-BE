package com.ticketon.ticketon.domain.eventitem.service;

import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import com.ticketon.ticketon.domain.eventitem.entity.dto.EventItemCreateRequestDto;
import com.ticketon.ticketon.domain.eventitem.entity.dto.EventItemResponseDto;
import com.ticketon.ticketon.domain.eventitem.repository.EventItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventItemService {

    private final EventItemRepository eventItemRepository;

    public Long addEventItem(EventItemCreateRequestDto dto) {
        return eventItemRepository.save(EventItem.builder()
                .title(dto.getTitle())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build()
        ).getId();
    }

    //
    public List<EventItemResponseDto> getEventItemList() {
        List<EventItem> eventItems = eventItemRepository.findAllByOrderByStartDateAsc();
        return eventItems.stream()
                .map(EventItemResponseDto::from)
                .toList();
    }

    public EventItemResponseDto getEventItemById(Long id) {
        Optional<EventItem> eventItem = eventItemRepository.findById(id);

        // 예외처리 나중에
        return EventItemResponseDto.from(eventItem.orElseThrow());
    }
}
