package com.talearnt.admin.event.repository;

import com.talearnt.admin.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event,Long> {
}
