package com.max_griffin_project.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "market")
public class Market {
    @Id
    private String marketId; 

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "market", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiveLine> liveLines = new ArrayList<>();
}
