package com.max_griffin_project.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import com.max_griffin_project.dto.HistoricalLineDto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "sport")
public class Sport {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID _id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "side", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Side> sides = new ArrayList<>();
}
