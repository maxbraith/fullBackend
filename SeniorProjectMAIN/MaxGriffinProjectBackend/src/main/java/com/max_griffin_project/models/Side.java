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
@Table(name = "side")
public class Side {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;

    @Column(name = "name", nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "aliases", joinColumns = @JoinColumn(name = "side_id"))
    @Column(name = "alias")
    private List<String> aliases = new ArrayList<>();

    @OneToMany(mappedBy = "side", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sequence> sequences = new ArrayList<>();

    @OneToMany(mappedBy = "side_a")
    private List<Match> homeMatches = new ArrayList<>();

    @OneToMany(mappedBy = "side_b")
    private List<Match> awayMatches = new ArrayList<>();

}