package com.max_griffin_project.controllers;

import java.util.Map;

import com.max_griffin_project.dto.RoleDto;
import com.max_griffin_project.dto.UserResponseDto;
import com.max_griffin_project.dto.LineMetricsDto;
import com.max_griffin_project.dto.ArbInfoDto;
import com.max_griffin_project.dto.EventDto;
import com.max_griffin_project.dto.LiveLineDto;
import com.max_griffin_project.models.Event;
import com.max_griffin_project.service.EventService;
import com.max_griffin_project.service.LiveLineService;
import com.max_griffin_project.service.EventService;
import com.max_griffin_project.service.UserService;
import com.max_griffin_project.models.LiveLine;


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
    final private EventService eventService;
    final private LiveLineService liveLineService;

    @Autowired
    public SelfController(UserService userService, EventService eventService, LiveLineService liveLineService) {
        this.userService = userService;
        this.eventService = eventService;
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
    public ResponseEntity<List<EventDto>> upcomingEvents(@AuthenticationPrincipal UserDetails userDetails){
        if (userDetails == null){
            System.err.println("Unauthorized request to /upcoming-events");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<EventDto> upcomingEvents = eventService.getUpcomingEvents();
        return ResponseEntity.ok(upcomingEvents);
    }

    @GetMapping("/all-livelines")
    public ResponseEntity<Map<String, Map<String, List<LiveLineDto>>>> allLiveLines(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(name = "sport") String sport){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<EventDto> upcomingEvents = eventService.getUpcomingEventsBySport(sport);
        if (upcomingEvents.contains(null) || upcomingEvents.isEmpty()){
            System.err.println("No upcoming events found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        Map<String, Map<String, List<LiveLineDto>>> allLines = liveLineService.getAllLines(upcomingEvents);

        return ResponseEntity.ok(allLines);
    }


    @GetMapping("/shop-livelines")
    public ResponseEntity<Map<String, List<LiveLineDto>>> shopLiveLines(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(name = "sport") String sport){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<EventDto> upcomingEvents = eventService.getUpcomingEventsBySport(sport);
        if (upcomingEvents.contains(null) || upcomingEvents.isEmpty()){
            System.err.println("No upcoming events found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        Map<String, List<LiveLineDto>> shoppedLines = liveLineService.shopLiveLines(upcomingEvents);

        return ResponseEntity.ok(shoppedLines);
    }

    @GetMapping("shop-livelines-metrics")
    public ResponseEntity<Map<String, List<LineMetricsDto>>> shopLiveLinesMetrics(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(name = "sport") String sport){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<EventDto> upcomingEvents = eventService.getUpcomingEventsBySport(sport);
        if (upcomingEvents.contains(null) || upcomingEvents.isEmpty()){
            System.err.println("No upcoming events found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        Map<String, List<LineMetricsDto>> shoppedMetrics = liveLineService.lineShopperWithMetrics(upcomingEvents);

        return ResponseEntity.ok(shoppedMetrics);
    }

    @GetMapping("arb-livelines")
    public ResponseEntity<Map<String, ArbInfoDto>> arbLiveLines(@AuthenticationPrincipal UserDetails userDetails){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<EventDto> upcomingEvents = eventService.getUpcomingEvents();
        if (upcomingEvents.contains(null) || upcomingEvents.isEmpty()){
            System.err.println("No upcoming events found");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        Map<String, ArbInfoDto> arbOpportunities = liveLineService.arbLiveLines(upcomingEvents);

        return ResponseEntity.ok(arbOpportunities);

    }
    





        

}
