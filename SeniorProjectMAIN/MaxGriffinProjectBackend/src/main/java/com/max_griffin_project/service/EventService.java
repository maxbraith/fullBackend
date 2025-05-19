package com.max_griffin_project.service;

import com.max_griffin_project.dto.EventDto;
import com.max_griffin_project.models.Event;
import com.max_griffin_project.models.UserEntity;
import com.max_griffin_project.repository.EventRepository;
import com.max_griffin_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EventService {
    final private EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public List<EventDto> getUpcomingEvents(){
        List<Event> upcomingEvent = eventRepository.findByStartTimeAfter(Instant.now());

        return upcomingEvent.stream()
                        .map(Event::toEventDto)
                        .toList();
    }

    public List<EventDto> getUpcomingEventsBySport(String sport){
        List<Event> upcomingEvent = eventRepository.findBySportAndStartTimeAfter(sport, Instant.now());

        return upcomingEvent.stream()
                        .map(Event::toEventDto)
                        .toList();
    }
    
}
