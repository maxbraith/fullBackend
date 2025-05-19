package com.max_griffin_project.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.max_griffin_project.dto.EventDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "event")
public class Event {
    @Id
    private String eventId;

    @Column(nullable = false)
    private String sport;

    @Column(nullable = false)
    private String homeTeam;

    @Column(nullable = false)
    private String awayTeam;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = true)
    private String eventStatus;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiveLine> liveLines = new ArrayList<>();

    public EventDto toEventDto(){
        return new EventDto(this.eventId, this.sport, this.homeTeam, this.awayTeam, this.startTime, this.eventStatus);
    }
}