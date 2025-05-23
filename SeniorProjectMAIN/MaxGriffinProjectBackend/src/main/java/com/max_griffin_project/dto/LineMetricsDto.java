package com.max_griffin_project.dto;

import java.time.Instant;
import java.util.UUID;

public record LineMetricsDto (
       UUID liveLineId,
       String event_name,
       String market_id,
       String bookmaker_id,
       String sport_id,
       String side_name,
       String price,
       Instant timestamp,
       Double impliedProbability, /*implied probability always the same */
       /*vig, true prob, Ev using the shopped lines */
       Double shoppedVig,
       Double shoppedTrueProbability,
       Double shoppedEv,
       /*vig, tru prob, Ev using the bookmaker's lines */
       Double bookmakerVig,
       Double bookmakerTrueProbability,
       Double trueEv



) {}
