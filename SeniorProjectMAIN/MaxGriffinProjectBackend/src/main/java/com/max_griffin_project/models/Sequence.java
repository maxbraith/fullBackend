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
@Table(name = "sequence")
public class Sequence {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "side_id", nullable = false)
    private Side side;

    @ManyToOne
    @JoinColumn(name = "market_id", nullable = false)
    private Market market;

    @ManyToOne
    @JoinColumn(name = "bookmaker_id", nullable = false)
    private Bookmaker bookmaker;

    @Column(name = "marekt_close", nullable = true)
    private Instant market_close;

    @Column(name = "live_flag", nullable = true)
    private Boolean live_flag;

    @OneToMany(mappedBy = "sequence", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Snapshot> snapshots = new ArrayList<>();
}