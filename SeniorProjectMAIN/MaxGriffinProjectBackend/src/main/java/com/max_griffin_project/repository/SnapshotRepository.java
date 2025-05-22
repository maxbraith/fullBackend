package com.max_griffin_project.repository;

import com.max_griffin_project.models.Snapshot;
import com.max_griffin_project.models.Sequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SnapshotRepository extends JpaRepository<Snapshot, UUID> {
    Optional<Snapshot> findTopBySequenceOrderByTimestampDesc(Sequence sequence);

    Optional<Snapshot> findTopBySequence_IdOrderByTimestampDesc(UUID sequenceId);

}
