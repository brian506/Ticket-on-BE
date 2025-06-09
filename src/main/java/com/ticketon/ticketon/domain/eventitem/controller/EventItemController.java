package com.ticketon.ticketon.domain.eventitem.controller;

import com.ticketon.ticketon.domain.eventitem.entity.dto.EventItemCreateRequestDto;
import com.ticketon.ticketon.domain.eventitem.entity.dto.EventItemResponseDto;
import com.ticketon.ticketon.domain.eventitem.service.EventItemService;
import com.ticketon.ticketon.global.constants.Urls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EventItemController {

    private final EventItemService eventItemService;

//    @PostMapping("/event-item")
//    public String addEventItem(@RequestBody EventItemCreateRequestDto dto) {
//        eventItemService.addEventItem(dto);
//        return "redirect:/events";
//    }

    @GetMapping(Urls.EVENTS)
    public String eventItemList(Model model) {
        List<EventItemResponseDto> eventItems = eventItemService.getEventItemList();
        model.addAttribute("eventItems", eventItems);
        return "/eventitem/test/eventItemList";
    }

    @GetMapping(Urls.EVENT_DETAIL)
    public String eventItemDetail(@PathVariable Long id, Model model) {
        EventItemResponseDto eventItem = eventItemService.getEventItemById(id);
        model.addAttribute("eventItem", eventItem);
        return "/eventitem/test/eventItem";
    }
}
