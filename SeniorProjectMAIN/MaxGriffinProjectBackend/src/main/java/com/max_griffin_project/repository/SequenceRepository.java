package com.max_griffin_project.repository;

import com.max_griffin_project.models.Sequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SequenceRepository extends JpaRepository<Sequence, UUID> {
    List<Sequence> findByLiveFlagTrue();

    List<Sequence> findByMatch_IdAndLiveFlagTrue(UUID match_id);

    List<Sequence> findByMatch_IdAndSide_IdAndLiveFlagTrue(UUID match_id, UUID entity_id);

    Sequence findByMatch_IdAndBookmaker_IdAndMarket_IdAndSide_Id(UUID match_id, String bookmaker_id, String market_id, UUID side_id);

}


/*findByEvent_EventIdAndBookmaker_BookmakerIdAndTeam */