package com.max_griffin_project.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.max_griffin_project.dto.MatchDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "match")
public class Match {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "side_a_id", nullable = false)
    private Side side_a;

    @ManyToOne
    @JoinColumn(name = "side_b_id", nullable = false)
    private Side side_b;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = true)
    private Instant start_date;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sequence> sequences = new ArrayList<>();

    @OneToOne(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private Details details;

    public MatchDto toMatchDto() {
        return new MatchDto(
                this.id,
                this.side_a.getId(),
                this.side_a.getName(),
                this.side_b.getId(),
                this.side_b.getName(),
                this.event.getId(),
                this.event.getName(),
                this.event.getSport().getId());
    }

}