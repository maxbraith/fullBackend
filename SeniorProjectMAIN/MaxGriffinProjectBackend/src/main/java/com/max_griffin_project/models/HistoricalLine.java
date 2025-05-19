package com.max_griffin_project.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import com.max_griffin_project.dto.HistoricalLineDto;


import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "historicalline")
public class HistoricalLine {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID historicallineId;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "market_id", nullable = false)
    private Market market;

    @ManyToOne
    @JoinColumn(name = "bookmaker_id", nullable = false)
    private Bookmaker bookmaker;

    @Column(nullable = false)
    private String sport;

    @Column(nullable = false)
    private String team;

    @Column(nullable = false)
    private String price;

    @Column(nullable = false)
    private Instant timestamp;

    public HistoricalLineDto toHistoricalLineDto(){
        return new HistoricalLineDto(
            this.historicallineId, 
            this.event.getEventId(), 
            this.market.getMarketId(), 
            this.bookmaker.getBookmakerId(), 
            this.sport,
            this.team, 
            this.price, 
            this.timestamp);
        
    }


}