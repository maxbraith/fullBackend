package com.max_griffin_project.service;

import com.max_griffin_project.dto.MatchDto;
import com.max_griffin_project.models.Match;
import com.max_griffin_project.models.UserEntity;
import com.max_griffin_project.repository.MatchRepository;
import com.max_griffin_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MatchService {
    final private MatchRepository matchRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<MatchDto> getUpcomingEvents() {
        List<Match> upcomingMatch = matchRepository.findByStartTimeAfter(Instant.now());

        return upcomingMatch.stream()
                .map(Match::toMatchDto)
                .toList();
    }

    public List<MatchDto> getUpcomingEventsBySport(String sport) {
        List<Match> upcomingEvent = matchRepository.findBySport_IdAndStartDateAfter(sport, Instant.now());

        return upcomingEvent.stream()
                .map(Match::toMatchDto)
                .toList();
    }

}
