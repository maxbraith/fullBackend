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
@Table(name = "details")
public class Details {
    @Id
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;

    @OneToMany(mappedBy = "details", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Detail> attributes = new ArrayList<>();
}