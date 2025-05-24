package com.max_griffin_project.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "venue")
public class Venue {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "venue_aliases", joinColumns = @JoinColumn(name = "side_id"))
    @Column(name = "alias")
    private List<String> aliases = new ArrayList<>();

}
