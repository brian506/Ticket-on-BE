package com.ticketon.ticketon.domain.waiting_queue.controller;

import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.global.constants.Urls;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class QueueViewController {

    @PostMapping(Urls.WAITING)
    public String waitingPage(Model model, @RequestParam Long eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        model.addAttribute("eventId", eventId);
        model.addAttribute("email", email);
        return "ticket/test/waiting";
    }
}
