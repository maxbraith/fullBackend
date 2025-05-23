package com.max_griffin_project.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.max_griffin_project.dto.LiveLineDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "snapshot")
public class Snapshot {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sequence_id", nullable = false)
    private Sequence sequence;

    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    public LiveLineDto toLiveLineDto() {
        return new LiveLineDto(
                this.id,
                this.sequence.getMatch().getId(),
                this.sequence.getEvent().getName(),
                this.sequence.getEvent().getSport().getId(),
                this.sequence.getSide().getId(),
                this.sequence.getSide().getName(),
                this.sequence.getMarket().getId(),
                this.sequence.getBookmaker().getId(),
                this.sequence.getMarket_close(),
                this.price,
                this.timestamp);
    }

}
