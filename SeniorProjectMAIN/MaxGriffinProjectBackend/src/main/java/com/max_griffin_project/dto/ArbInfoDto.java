package com.max_griffin_project.dto;

import java.time.Instant;
import java.util.UUID;

public record ArbInfoDto (
       UUID match_id,
       String homeTeam_name,
       String home_price,
       String homeBookmaker_id,
       String sport_id,
       Double home_wager,
       String away_name,
       String away_price,
       String awayBookmaker_id,
       Double away_wager,
       Instant startTime,
       Double expectedProfit


) {}