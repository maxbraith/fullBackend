package com.max_griffin_project.dto;

import java.time.Instant;

public record EventDto (
       String eventId,
       String sport,
       String homeTeam,
       String awayTeam,
       Instant startTime,
       String eventStatus


) {}
