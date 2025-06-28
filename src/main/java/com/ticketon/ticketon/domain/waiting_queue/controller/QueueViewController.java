package com.ticketon.ticketon.domain.waiting_queue.controller;

import com.ticketon.ticketon.domain.ticket.entity.dto.TicketPurchaseRequest;
import com.ticketon.ticketon.global.constants.Urls;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class QueueViewController {

    @PostMapping(Urls.WAITING)
    public String waitingPage(Model model, TicketPurchaseRequest request) {
        model.addAttribute("ticketTypeId", request.getTicketTypeId());
        model.addAttribute("quantity", request.getQuantity());
        return "ticket/test/waiting";
    }
}
