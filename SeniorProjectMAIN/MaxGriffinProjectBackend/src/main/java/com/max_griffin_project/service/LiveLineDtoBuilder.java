package com.max_griffin_project.service;

import com.max_griffin_project.dto.LiveLineDto;
import com.max_griffin_project.models.Sequence;
import com.max_griffin_project.models.Snapshot;
import com.max_griffin_project.models.UserEntity;
import com.max_griffin_project.repository.SequenceRepository;
import com.max_griffin_project.repository.SnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.time.Instant;
import java.util.List;

@Service
public class LiveLineDtoBuilder {

    @Autowired
    private SequenceRepository sequenceRepo;

    @Autowired
    private SnapshotRepository snapshotRepo;

    public List<LiveLineDto> getCurrentLiveLines() {
        List<Sequence> sequences = sequenceRepo.findByLiveFlagTrue();

        return sequences.stream()
                .map(seq -> {
                    Optional<Snapshot> latest = snapshotRepo.findTopBySequenceOrderByTimestampDesc(seq);
                    if (latest.isEmpty())
                        return null;
                    Snapshot snap = latest.get();

                    return new LiveLineDto(
                            seq.getId(),
                            seq.getMatch().getId(),
                            seq.getSide().getId(),
                            seq.getBookmaker().getId(),
                            seq.getSide().getName(),
                            snap.getPrice().toString(),
                            snap.getTimestamp());
                })
                .filter(Objects::nonNull)
                .toList();
    }
}