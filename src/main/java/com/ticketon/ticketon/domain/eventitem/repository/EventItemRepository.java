package com.ticketon.ticketon.domain.eventitem.repository;

import com.ticketon.ticketon.domain.eventitem.entity.EventItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventItemRepository extends JpaRepository<EventItem, Long> {
    List<EventItem> findAllByOrderByStartDateAsc();
}
