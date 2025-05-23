package com.max_griffin_project.service;

import com.max_griffin_project.dto.MatchDto;
import com.max_griffin_project.dto.LiveLineDto;
import com.max_griffin_project.dto.LineMetricsDto;
import com.max_griffin_project.dto.ArbInfoDto;
import com.max_griffin_project.models.Event;
import com.max_griffin_project.models.Sequence;
import com.max_griffin_project.models.Snapshot;
import com.max_griffin_project.models.UserEntity;
import com.max_griffin_project.repository.MatchRepository;
import com.max_griffin_project.repository.SequenceRepository;
import com.max_griffin_project.repository.SnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class LiveLineService {
    final private SequenceRepository sequenceRepository;
    final private SnapshotRepository snapshotRepository;

    @Autowired
    public LiveLineService(SequenceRepository sequenceRepository, SnapshotRepository snapshotRepository) {
        this.sequenceRepository = sequenceRepository;
        this.snapshotRepository = snapshotRepository;
    }

    public Map<UUID, List<LiveLineDto>> shopLiveLines(List<MatchDto> upcomingMatches) {
        Map<UUID, List<LiveLineDto>> shopMap = new HashMap<>();

        for (MatchDto match : upcomingMatches) {
            if (match == null || match.id() == null) {
                System.err.println("Error null");
                continue;
            }

            List<Sequence> liveLinesHome = sequenceRepository.findByMatch_IdAndSide_IdAndLiveFlagTrue(match.id(),
                    match.side_a_id());
            List<Sequence> liveLinesAway = sequenceRepository.findByMatch_IdAndSide_IdAndLiveFlagTrue(match.id(),
                    match.side_b_id());

            if (liveLinesHome.isEmpty()) {
                System.err.println("Error: No home live lines found for matchID: " + match.id());
                continue;
            }

            if (liveLinesAway.isEmpty()) {
                System.err.println("Error: No away live lines found for matchID: " + match.id());
                continue;
            }

            LiveLineDto shopHomeDto = null;
            LiveLineDto shopAwayDto = null;

            for (Sequence liveLine : liveLinesHome) {
                Optional<Snapshot> latestSnap = snapshotRepository
                        .findTopBySequence_IdOrderByTimestampDesc(liveLine.getId());
                LiveLineDto liveLineDto = latestSnap.map(Snapshot::toLiveLineDto).orElse(null);
                if (shopHomeDto == null || isBetterOdds(liveLineDto.price(), shopHomeDto.price())) {
                    shopHomeDto = liveLineDto;
                }
            }
            for (Sequence liveLine : liveLinesAway) {
                Optional<Snapshot> latestSnap = snapshotRepository
                        .findTopBySequence_IdOrderByTimestampDesc(liveLine.getId());
                LiveLineDto liveLineDto = latestSnap.map(Snapshot::toLiveLineDto).orElse(null);
                if (shopHomeDto == null || isBetterOdds(liveLineDto.price(), shopAwayDto.price())) {
                    shopAwayDto = liveLineDto;
                }
            }
            List<LiveLineDto> optimalLines = new ArrayList<>();
            optimalLines.add(shopHomeDto);
            optimalLines.add(shopAwayDto);

            shopMap.put(match.id(), optimalLines);
        }

        return shopMap;
    }

    public Map<UUID, List<LineMetricsDto>> lineShopperWithMetrics(List<MatchDto> upcomingMatches) {
        Map<UUID, List<LineMetricsDto>> metricsMap = new HashMap<>();

        for (MatchDto match : upcomingMatches) {
            if (match == null || match.id() == null) {
                System.err.println("Error null");
                continue;
            }

            /*get live sequences from this match, home and away */
            List<Sequence> liveLinesHome = sequenceRepository.findByMatch_IdAndSide_IdAndLiveFlagTrue(match.id(),
                    match.side_a_id());
            List<Sequence> liveLinesAway = sequenceRepository.findByMatch_IdAndSide_IdAndLiveFlagTrue(match.id(),
                    match.side_b_id());

            if (liveLinesHome.isEmpty()) {
                System.err.println("Error: No home live lines found for eventId " + match.id());
                continue;
            }

            if (liveLinesAway.isEmpty()) {
                System.err.println("Error: No away live lines found for eventID: " + match.id());
                continue;
            }

            LiveLineDto shopHomeDto = null;
            LiveLineDto shopAwayDto = null;

            for (Sequence liveLine : liveLinesHome) {
                /*find most recent snapshot from the home line */
                Optional<Snapshot> latestSnap = snapshotRepository
                        .findTopBySequence_IdOrderByTimestampDesc(liveLine.getId());
                LiveLineDto liveLineDto = latestSnap.map(Snapshot::toLiveLineDto).orElse(null);
                /*lineshop what is available */
                if (shopHomeDto == null || isBetterOdds(liveLineDto.price(), shopHomeDto.price())) {
                    shopHomeDto = liveLineDto;
                }
            }
            for (Sequence liveLine : liveLinesAway) {
                Optional<Snapshot> latestSnap = snapshotRepository
                        .findTopBySequence_IdOrderByTimestampDesc(liveLine.getId());
                LiveLineDto liveLineDto = latestSnap.map(Snapshot::toLiveLineDto).orElse(null);
                if (shopHomeDto == null || isBetterOdds(liveLineDto.price(), shopAwayDto.price())) {
                    shopAwayDto = liveLineDto;
                }
            }
            List<LineMetricsDto> optimalLines = new ArrayList<>();

            Double breakEvenHome = calculateImpliedProbability(shopHomeDto.price());
            Double breakEvenAway = calculateImpliedProbability(shopAwayDto.price());
            Double shoppedVig = calculateVig(breakEvenHome, breakEvenAway);

            Double trueProbHomeShopped = calculateTrueProbability(breakEvenHome, breakEvenAway);
            Double trueProbAwayShopped = calculateTrueProbability(breakEvenAway, breakEvenHome);

            Double homeShoppedEV = calculateEV(trueProbHomeShopped, trueProbAwayShopped, shopHomeDto.price());
            Double awayShoppedEV = calculateEV(trueProbAwayShopped, trueProbHomeShopped, shopAwayDto.price());


            /* find the opposing sequence by bookmaker - find away line to compare to home line */
            Sequence bookmakerAwayShoppedHome = sequenceRepository.
                    findByMatch_IdAndBookmaker_IdAndMarket_IdAndSide_Id(shopHomeDto.match_id(), shopHomeDto.bookmaker_id(),
                            shopHomeDto.market_id(), shopAwayDto.side_id());

            Sequence bookmakerHomeShoppedAway = sequenceRepository.
                    findByMatch_IdAndBookmaker_IdAndMarket_IdAndSide_Id(shopAwayDto.match_id(), shopAwayDto.bookmaker_id(),
                            shopAwayDto.market_id(), shopHomeDto.side_id());

            /*find true vig of the shopped home line */
            Optional<Snapshot> latestSnapBookmakerAway = snapshotRepository.findTopBySequence_IdOrderByTimestampDesc(bookmakerAwayShoppedHome.getId());
            LiveLineDto bookmakerAwayShoppedHomeDto = latestSnapBookmakerAway.map(Snapshot::toLiveLineDto).orElse(null);

            /*find true vig of the shopped away line */
            Optional<Snapshot> latestSnapBookmakerHome = snapshotRepository.findTopBySequence_IdOrderByTimestampDesc(bookmakerHomeShoppedAway.getId());
            LiveLineDto bookmakerHomeShoppedAwayDto = latestSnapBookmakerHome.map(Snapshot::toLiveLineDto).orElse(null);

            Double breakEvenBookmakerAway = calculateImpliedProbability(bookmakerAwayShoppedHomeDto.price());
            Double breakEvenBookmakerHome = calculateImpliedProbability(bookmakerHomeShoppedAwayDto.price());
            Double bookmakerHomeVig = calculateVig(breakEvenHome, breakEvenBookmakerAway);
            Double bookmakerAwayVig = calculateVig(breakEvenAway, breakEvenBookmakerHome);

            Double trueProbBookmakerHome = calculateTrueProbability(breakEvenHome, breakEvenBookmakerAway);
            Double trueProbBookmakerHomeOpp = calculateTrueProbability(breakEvenBookmakerAway, breakEvenHome);
            Double trueProbBookmakerAway = calculateTrueProbability(breakEvenAway, breakEvenBookmakerHome);
            Double trueProbBookmakerAwayOpp = calculateTrueProbability(breakEvenBookmakerHome, breakEvenAway);

            double homeTrueEV = calculateEV(trueProbHomeShopped, trueProbBookmakerHomeOpp, shopHomeDto.price());
            double awayTrueEv = calculateEV(trueProbAwayShopped, trueProbBookmakerAwayOpp, shopAwayDto.price());

            LineMetricsDto metricsHomeDto = new LineMetricsDto(
                    shopHomeDto.snapshot_id(),
                    shopHomeDto.event_name(),
                    shopHomeDto.market_id(),
                    shopHomeDto.bookmaker_id(),
                    shopHomeDto.sport_id(),
                    shopHomeDto.side_name(),
                    shopHomeDto.price(),
                    shopHomeDto.timestamp(),
                    breakEvenHome,
                    shoppedVig,
                    trueProbHomeShopped,
                    homeShoppedEV,
                    bookmakerHomeVig,
                    trueProbBookmakerHome,
                    homeTrueEV);

            LineMetricsDto metricsAwayDto = new LineMetricsDto(
                    shopAwayDto.snapshot_id(),
                    shopAwayDto.event_name(),
                    shopAwayDto.market_id(),
                    shopAwayDto.bookmaker_id(),
                    shopAwayDto.sport_id(),
                    shopAwayDto.side_name(),
                    shopAwayDto.price(),
                    shopAwayDto.timestamp(),
                    breakEvenAway,
                    shoppedVig,
                    trueProbAwayShopped,
                    awayShoppedEV,
                    bookmakerAwayVig,
                    trueProbBookmakerAway,
                    awayTrueEv);

            optimalLines.add(metricsHomeDto);
            optimalLines.add(metricsAwayDto);

            metricsMap.put(match.id(), optimalLines);
        }

        return metricsMap;
    }


    /* find all sequences associated with each match given sport */
    public Map<UUID, Map<String, List<LiveLineDto>>> getAllLines(List<MatchDto> upcomingMatches) {
        Map<UUID, Map<String, List<LiveLineDto>>> lineMap = new HashMap<>();

        for (MatchDto match : upcomingMatches) {
            if (match == null || match.id() == null) {
                System.err.println("Error null");
                continue;
            }

            List<Sequence> liveLines = sequenceRepository.findByMatch_IdAndLiveFlagTrue(match.id());
            Map<String, List<LiveLineDto>> bookMap = new HashMap<>();

            for (Sequence liveLine : liveLines) {
                Optional<Snapshot> latestSnap = snapshotRepository
                        .findTopBySequence_IdOrderByTimestampDesc(liveLine.getId());
                LiveLineDto liveLineDto = latestSnap.map(Snapshot::toLiveLineDto).orElse(null);
                bookMap.computeIfAbsent(liveLineDto.bookmaker_id(), k -> new ArrayList<>()).add(liveLineDto);
            }
            lineMap.put(match.id(), bookMap);
        }

        return lineMap;

    }


    /* look for arbs */
    public Map<UUID, ArbInfoDto> arbLiveLines(List<MatchDto> upcomingMatches) {
        Map<UUID, ArbInfoDto> arbMap = new HashMap<>();

        for (MatchDto match : upcomingMatches) {
            if (match == null || match.id() == null) {
                System.err.println("Error null");
                continue;
            }

            List<Sequence> liveLinesHome = sequenceRepository.findByMatch_IdAndSide_IdAndLiveFlagTrue(match.id(), match.side_a_id());
            List<Sequence> liveLinesAway = sequenceRepository.findByMatch_IdAndSide_IdAndLiveFlagTrue(match.id(), match.side_b_id());

            Double homeBreakEven;
            Double awayBreakEven;
            Double vig;

            for (Sequence liveLineHome : liveLinesHome) {
                Optional<Snapshot> latestSnapHome = snapshotRepository
                        .findTopBySequence_IdOrderByTimestampDesc(liveLineHome.getId());
                LiveLineDto liveLineHomeDto = latestSnapHome.map(Snapshot::toLiveLineDto).orElse(null);
                homeBreakEven = calculateImpliedProbability(liveLineHomeDto.price());
                for (Sequence liveLineAway : liveLinesAway) {
                    Optional<Snapshot> latestSnapAway = snapshotRepository
                            .findTopBySequence_IdOrderByTimestampDesc(liveLineAway.getId());
                    LiveLineDto liveLineAwayDto = latestSnapAway.map(Snapshot::toLiveLineDto).orElse(null);
                    awayBreakEven = calculateImpliedProbability(liveLineAwayDto.price());
                    vig = calculateVig(homeBreakEven, awayBreakEven);
                    if (vig < 0) {
                        Double wagerSplit = calculateArbWagerSplit(liveLineHomeDto.price(), liveLineAwayDto.price());
                        Double wagerSplit2 = calculateArbWagerSplit(liveLineAwayDto.price(), liveLineHomeDto.price());
                        ArbInfoDto arb = new ArbInfoDto(match.id(),
                                liveLineHomeDto.side_name(),
                                liveLineHomeDto.price(),
                                liveLineHomeDto.bookmaker_id(),
                                liveLineHomeDto.sport_id(),
                                wagerSplit,
                                liveLineAwayDto.side_name(),
                                liveLineAwayDto.price(),
                                liveLineAwayDto.bookmaker_id(),
                                wagerSplit2,
                                match.start_time(),
                                vig);
                        arbMap.put(match.id(), arb);
                    }

                }
            }
        }

        return arbMap;
    }

    private boolean isBetterOdds(String newOdds, String existingOdds) {
        if (existingOdds == null) {
            return true;
        }
        double newPrice = Double.parseDouble(newOdds);
        double currentPrice = Double.parseDouble(existingOdds);
        return newPrice > currentPrice;

    }

    private double calculateImpliedProbability(String line) {
        double price = Double.parseDouble(line);
        double impliedProbability;
        if (price < 0) {
            impliedProbability = Math.abs(price) / (Math.abs(price) + 100);
        } else {
            impliedProbability = 100 / (price + 100);
        }
        return impliedProbability;
    }

    private double calculateVig(double be1, double be2) {
        double vig = (be1 + be2) - 1;
        return vig;
    }

    private double calculateTrueProbability(double be2Process, double beOtherSide) {
        double vig = be2Process + beOtherSide;
        double normalizer = 1 / vig;
        double trueProb = normalizer * be2Process;
        return trueProb;

    }

    private double calculateEV(double noVigProcess, double noVigOtherSide, String line) {
        double price = Double.parseDouble(line);
        double eV;

        if (price > 0) {
            eV = (noVigProcess * price) - (noVigOtherSide * 100);
        } else {
            eV = (noVigProcess * 100) - (noVigOtherSide * Math.abs(price));
        }
        return eV;
    }

    private double calculateArbWagerSplit(String lineForWager, String otherLine) {
        double lineWager = Double.parseDouble(lineForWager);
        double lineOther = Double.parseDouble(otherLine);

        Double lineWagerDec;
        Double lineOtherDec;

        if (lineWager > 0) {
            lineWagerDec = (1 + lineWager) / 100;
        } else {
            lineWagerDec = (1 + 100) / Math.abs(lineWager);
        }

        if (lineOther > 0) {
            lineOtherDec = (1 + lineWager) / 100;
        } else {
            lineOtherDec = (1 + 100) / Math.abs(lineWager);
        }

        double toStake = (100 * lineOtherDec) / (lineWagerDec + lineOtherDec);
        return toStake;

    }

}