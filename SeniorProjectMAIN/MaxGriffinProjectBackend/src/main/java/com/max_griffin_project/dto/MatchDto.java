package com.max_griffin_project.dto;

import java.util.UUID;

import java.time.Instant;

public record MatchDto(
        UUID id,
        UUID side_a_id,
        String side_aName,
        UUID side_b_id,
        String side_bName,
        UUID event_id,
        String eventName,
        String sport_id) {
}
/* updated */
