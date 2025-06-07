package com.ticketon.ticketon.domain.eventitem.controller;

import com.ticketon.ticketon.domain.eventitem.entity.dto.EventItemCreateRequestDto;
import com.ticketon.ticketon.domain.eventitem.entity.dto.EventItemResponseDto;
import com.ticketon.ticketon.domain.eventitem.service.EventItemService;
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
public class EventItemTestController {

    private final EventItemService eventItemService;

    @GetMapping("/event-item/add")
    public String addEventItemPage() {
        return "/eventitem/test/addEventItem";
    }

    @PostMapping("/event-item")
    public String addEventItem(@RequestBody EventItemCreateRequestDto dto) {
        eventItemService.addEventItem(dto);
        return "redirect:/events";
    }

    @GetMapping("/events")
    public String eventItemList(Model model) {
        List<EventItemResponseDto> eventItems = eventItemService.getEventItemList();
        model.addAttribute("eventItems", eventItems);
        return "/eventitem/test/eventItemList";
    }

    @GetMapping("/event/{id}")
    public String eventItemList(@PathVariable Long id, Model model) {
        EventItemResponseDto eventItem = eventItemService.getEventItemById(id);
        model.addAttribute("eventItem", eventItem);
        return "/eventitem/test/eventItem";
    }
}
