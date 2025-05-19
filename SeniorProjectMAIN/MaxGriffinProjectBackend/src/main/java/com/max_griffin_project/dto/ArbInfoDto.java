package com.max_griffin_project.dto;

import java.time.Instant;

public record ArbInfoDto (
       String eventId,
       String homeTeam,
       String homeTeamLine,
       String homeTeamBookmaker,
       String sport,
       Double homeTeamWager,
       String awayTeam,
       String awayTeamLine,
       String awayTeamBookmaker,
       Double awayTeamWager,
       Instant startTime,
       Double expectedProfit


) {}