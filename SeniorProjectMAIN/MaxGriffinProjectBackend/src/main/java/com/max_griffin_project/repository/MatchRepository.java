package com.max_griffin_project.repository;

import com.max_griffin_project.models.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {
    List<Match> findByStartDateAfter(Instant now);

    List<Match> findByEvent_Sport_IdAndStartDateAfter(String sportId, Instant now);

}
