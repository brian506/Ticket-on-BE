package com.ticketon.ticketon.domain.eventitem.entity;

import com.ticketon.ticketon.domain.ticket.entity.TicketType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Table(name = "event_items")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_item_id", nullable = false)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "eventItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketType> ticketTypes;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false)
    private EventItemStatus eventItemStatus;

}
