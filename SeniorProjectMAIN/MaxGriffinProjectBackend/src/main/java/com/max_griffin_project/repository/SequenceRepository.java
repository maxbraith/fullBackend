package com.max_griffin_project.repository;

import com.max_griffin_project.models.Sequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SequenceRepository extends JpaRepository<Sequence, UUID> {
    List<Sequence> findByLiveFlagTrue();

    List<Sequence> findBySport_IdAndMatch_IdAndEntity_IdAndLiveFlagTrue(UUID sport_id, UUID match_id, UUID entity_id);

}
