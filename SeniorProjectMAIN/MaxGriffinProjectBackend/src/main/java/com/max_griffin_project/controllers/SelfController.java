package com.max_griffin_project.controllers;

import java.util.Map;

import com.max_griffin_project.dto.RoleDto;
import com.max_griffin_project.dto.UserResponseDto;
import com.max_griffin_project.dto.LineMetricsDto;
import com.max_griffin_project.dto.ArbInfoDto;
import com.max_griffin_project.dto.MatchDto;
import com.max_griffin_project.dto.LiveLineDto;
import com.max_griffin_project.models.Event;
import com.max_griffin_project.service.MatchService;
import com.max_griffin_project.service.LiveLineService;
import com.max_griffin_project.service.MatchService;
import com.max_griffin_project.service.UserService;
import com.max_griffin_project.models.Sequence;
import com.max_griffin_project.models.Snapshot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/@me")
public class SelfController {
    final private UserService userService;
    final private MatchService matchService;
    final private LiveLineService liveLineService;

    @Autowired
    public SelfController(UserService userService, MatchService matchService, LiveLineService liveLineService) {
        this.userService = userService;
        this.matchService = matchService;
        this.liveLineService = liveLineService;
    }

    /*
     * '@AuthenticationPrincipal UserDetails userDetails' is required whenever
     * access to a user's details are required.
     */
    @GetMapping
    public ResponseEntity<UserResponseDto> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserResponseDto userResponse = new UserResponseDto(userDetails.getUsername());
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDto>> getUserRoles(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<RoleDto> roles = userService.getUserRoles(userDetails.getUsername());
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/upcoming-events")
    public ResponseEntity<List<MatchDto>> upcomingMatches(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            System.err.println("Unauthorized request to /upcoming-events");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<MatchDto> upcomingMatches = matchService.getUpcomingMatches();
        return ResponseEntity.ok(upcomingMatches);
    }

    @GetMapping("/all-livelines")
    public ResponseEntity<Map<UUID, Map<String, List<LiveLineDto>>>> allLiveLines(
            @AuthenticationPrincipal UserDetails userDetails, @RequestParam(name = "sport") String sport) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<MatchDto> upcomingMatches = matchService.getUpcomingMatchesBySport(sport);
        if (upcomingMatches.contains(null) || upcomingMatches.isEmpty()) {
            System.err.println("No upcoming events found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        Map<UUID, Map<String, List<LiveLineDto>>> allLines = liveLineService.getAllLines(upcomingMatches);

        return ResponseEntity.ok(allLines);
    }

    @GetMapping("/shop-livelines")
    public ResponseEntity<Map<UUID, List<LiveLineDto>>> shopLiveLines(
            @AuthenticationPrincipal UserDetails userDetails, @RequestParam(name = "sport") String sport) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<MatchDto> upcomingMatches = matchService.getUpcomingMatchesBySport(sport);
        if (upcomingMatches.contains(null) || upcomingMatches.isEmpty()) {
            System.err.println("No upcoming events found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        Map<UUID, List<LiveLineDto>> shoppedLines = liveLineService.shopLiveLines(upcomingMatches);

        return ResponseEntity.ok(shoppedLines);
    }

    @GetMapping("shop-livelines-metrics")
    public ResponseEntity<Map<UUID, List<LineMetricsDto>>> shopLiveLinesMetrics(
            @AuthenticationPrincipal UserDetails userDetails, @RequestParam(name = "sport") String sport) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<MatchDto> upcomingMatches = matchService.getUpcomingMatchesBySport(sport);
        if (upcomingMatches.contains(null) || upcomingMatches.isEmpty()) {
            System.err.println("No upcoming events found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        Map<UUID, List<LineMetricsDto>> shoppedMetrics = liveLineService.lineShopperWithMetrics(upcomingMatches);

        return ResponseEntity.ok(shoppedMetrics);
    }

    @GetMapping("arb-livelines")
    public ResponseEntity<Map<UUID, ArbInfoDto>> arbLiveLines(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<MatchDto> upcomingMatches = matchService.getUpcomingMatches();
        if (upcomingMatches.contains(null) || upcomingMatches.isEmpty()) {
            System.err.println("No upcoming events found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        Map<UUID, ArbInfoDto> arbOpportunities = liveLineService.arbLiveLines(upcomingMatches);

        return ResponseEntity.ok(arbOpportunities);

    }

}
