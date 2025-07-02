package com.ticketon.ticketon.domain.eventitem.repository;

import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventItemRepository extends JpaRepository<EventItem, Long> {
    List<EventItem> findAllByOrderByStartDateAsc();
    Optional<EventItem> findByTicketTypes_id(Long ticketTypeId);
}
