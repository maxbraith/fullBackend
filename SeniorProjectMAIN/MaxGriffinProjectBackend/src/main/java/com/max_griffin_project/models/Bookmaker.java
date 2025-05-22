package com.max_griffin_project.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "bookmaker")
public class Bookmaker {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "bookmaker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sequence> sequences = new ArrayList<>();
}