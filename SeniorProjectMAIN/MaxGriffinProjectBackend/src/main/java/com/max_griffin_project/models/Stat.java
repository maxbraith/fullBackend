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
@Table(name = "stat")
public class Stat {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "stats_id", nullable = false)
    private Stats stats;

    @Column(nullable = false)
    private String key;

    @Column(nullable = true)
    private String value;
}
