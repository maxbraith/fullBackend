package com.max_griffin_project.dto;

import java.time.Instant;
import java.util.UUID;
import java.math.BigDecimal;

public record LiveLineDto(
              UUID snapshot_id,
              UUID match_id,
              String sport_id,
              String event_name,
              UUID side_id,
              String side_name,
              String market_id,
              String bookmaker_id,
              Instant market_close,
              String price,
              Instant timestamp

) {
    public double priceAsDouble(){
        return Double.parseDouble(price);
    }

    public BigDecimal priceAsBigDecimal(){
        return new BigDecimal(price);
    }
}

