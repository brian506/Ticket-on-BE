package com.ticketon.ticketon.domain.eventitem.service;

import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import com.ticketon.ticketon.domain.eventitem.dto.EventItemResponse;
import com.ticketon.ticketon.domain.eventitem.repository.EventItemRepository;
import com.ticketon.ticketon.exception.custom.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventItemService {

    private final EventItemRepository eventItemRepository;

    //
    public List<EventItemResponse> getEventItemList() {
        List<EventItem> eventItems = eventItemRepository.findAllByOrderByStartDateAsc();
        return eventItems.stream()
                .map(EventItemResponse::from)
                .toList();
    }



    public EventItemResponse getEventItemById(Long id) {
        EventItem eventItem = eventItemRepository.findById(id).orElseThrow(() -> new DataNotFoundException("해당 공연(이벤트)를 찾을 수 없습니다 (id=" + id + ""));

        return EventItemResponse.from(eventItem);
    }
}
