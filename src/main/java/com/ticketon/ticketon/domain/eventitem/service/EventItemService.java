package com.ticketon.ticketon.domain.eventitem.service;

import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import com.ticketon.ticketon.domain.eventitem.dto.EventItemResponse;
import com.ticketon.ticketon.domain.eventitem.repository.EventItemRepository;
import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import com.ticketon.ticketon.domain.ticket.repository.TicketTypeRepository;
import com.ticketon.ticketon.exception.custom.NotFoundDataException;
import com.ticketon.ticketon.utils.OptionalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventItemService {

    private final EventItemRepository eventItemRepository;

    public String getTitleByTicketTypeId(Long ticketTypeId) {
         EventItem eventItem = OptionalUtil.getOrElseThrow(eventItemRepository.findByTicketTypes_id(ticketTypeId),"존재하지 않은 이벤트입니다");
         return eventItem.getTitle();

    }
    //
    public List<EventItemResponse> getEventItemList() {
        List<EventItem> eventItems = eventItemRepository.findAllByOrderByStartDateAsc();
        return eventItems.stream()
                .map(EventItemResponse::from)
                .toList();
    }

    public EventItemResponse getEventItemById(Long id) {
        EventItem eventItem = eventItemRepository.findById(id).orElseThrow(() -> new NotFoundDataException("해당 공연(이벤트)를 찾을 수 없습니다 (id=" + id + ""));

        return EventItemResponse.from(eventItem);
    }
}
