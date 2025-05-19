package com.max_griffin_project.dto;

import java.time.Instant;
import java.util.UUID;

public record LineMetricsDto (
       UUID liveLineId,
       String eventId,
       String marketId,
       String bookmakerId,
       String sport,
       String team,
       String price,
       Instant timestamp,
       Double shoppedImpliedProbability,
       Double shoppedVig,
       Double shoppedTrueProbability,
       Double shoppedEv,
       Double bookmakerVig,
       Double bookmakerTrueProbability,
       Double trueEv



) {}
