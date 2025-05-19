package com.max_griffin_project.repository;

import com.max_griffin_project.models.LiveLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.max_griffin_project.models.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface LiveLineRepository extends JpaRepository<LiveLine, UUID>{
    List<LiveLine> findByEvent_EventIdAndTeam(String eventId, String team);
    LiveLine findByEvent_EventIdAndBookmaker_BookmakerIdAndTeam(String eventId, String bookmakerId, String team);
    List<LiveLine> findByEvent_EventId(String eventId);
}
