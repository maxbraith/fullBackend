package com.max_griffin_project.dto;

import java.time.Instant;
import java.util.UUID;

public record LiveLineDto(
              UUID snapshot_id,
              UUID match_id,
              String event_name,
              String side_name,
              String market_name,
              Instant market_close,
              String price,
              Instant timestamp

) {
}
