package com.max_griffin_project.repository;

import com.max_griffin_project.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, String>{
    List<Event> findByStartTimeAfter(Instant now);
    List<Event> findBySportAndStartTimeAfter(String sport, Instant now);
    
}
