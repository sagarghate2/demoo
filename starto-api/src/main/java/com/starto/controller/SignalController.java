package com.starto.controller;

import com.starto.model.NearbySpace;
import com.starto.model.Signal;
import com.starto.model.User;
import com.starto.service.SignalService;
import com.starto.service.UserService;
import lombok.RequiredArgsConstructor;
import com.starto.service.WebSocketService;
import com.starto.dto.SignalRequestDTO;
import com.starto.dto.NearbyUserDTO;
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Signal and Space management controller.
 */
@Tag(name = "Signals & Spaces", description = "Endpoints for creating and discovering ecosystem items")
@RestController
@RequestMapping("/api/signals")
@RequiredArgsConstructor
public class SignalController {

    private final SignalService signalService;
    private final UserService userService;
    private final WebSocketService webSocketService;

    @Operation(summary = "Create a signal", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<?> createSignal(
            Authentication authentication,
            @Valid @RequestBody SignalRequestDTO dto) {

        if (authentication == null || authentication.getPrincipal() == null)
            return ResponseEntity.status(401).build();

        String firebaseUid = authentication.getPrincipal().toString();

        return userService.getUserByFirebaseUid(firebaseUid)
                .map(user -> {
                    try {
                        signalService.validateSignalCreation(user);
                    } catch (RuntimeException ex) {
                        return ResponseEntity.status(403).body(Map.of(
                                "error", ex.getMessage(),
                                "upgradeUrl", "/api/subscriptions/upgrade"
                        ));
                    }

                    Signal signal = Signal.builder()
                            .type(dto.getType())
                            .title(dto.getTitle())
                            .description(dto.getDescription())
                            .stage(dto.getStage())
                            .city(dto.getCity())
                            .state(dto.getState())
                            .category(dto.getCategory())
                            .seeking(dto.getSeeking())
                            .signalStrength(dto.getSignalStrength() != null ? dto.getSignalStrength() : "normal")
                            .timelineDays(dto.getTimelineDays())
                            .lat(dto.getLat())
                            .lng(dto.getLng())
                            .address(dto.getAddress())
                            .user(user)
                            .build();

                    Signal saved = signalService.createSignal(signal);
                    webSocketService.send("/topic/signals", saved);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @Operation(summary = "Nearby map data")
    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyMapData(
            @RequestParam String lat,
            @RequestParam String lng,
            @RequestParam(required = false, defaultValue = "10") double radiusKm,
            @RequestParam(required = false) String role) {
        
        System.out.println("[SignalController] nearby request received. Lat: " + lat + ", Lng: " + lng + ", Role: " + role);

        double latitude;
        double longitude;
        try {
            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(lng);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid coordinates"));
        }

        List<Signal> nearbySignals = signalService.getNearbySignals(latitude, longitude, radiusKm, role);
        List<NearbySpace> nearbySpaces = signalService.getNearbySpaces(latitude, longitude, radiusKm, role);
        List<NearbyUserDTO> nearbyUsers = signalService.getNearbyUsers(latitude, longitude, radiusKm, role);

        Map<String, Object> responseData = new java.util.LinkedHashMap<>();
        responseData.put("latitude", latitude);
        responseData.put("longitude", longitude);
        responseData.put("radiusKm", radiusKm);
        responseData.put("signals", nearbySignals);
        responseData.put("nearbySpaces", nearbySpaces);
        responseData.put("users", nearbyUsers);

        return ResponseEntity.ok(responseData);
    }

    @GetMapping
    public ResponseEntity<?> getSignals(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String seeking,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page) {

        if (username != null && seeking != null) {
            return ResponseEntity.ok(signalService.getSignalsByUsernameAndSeeking(username, seeking));
        }
        if (username != null) {
            return ResponseEntity.ok(signalService.searchSignalsByUsername(username));
        }
        if (seeking != null && city != null) {
            return ResponseEntity.ok(signalService.getSignalsBySeekingAndCity(seeking, city));
        }
        if (seeking != null) {
            return ResponseEntity.ok(signalService.getSignalsBySeeking(seeking));
        }
        if (city != null) {
            return ResponseEntity.ok(signalService.getSignalsByCity(city));
        }

        return ResponseEntity.ok(signalService.getSignalsFeed(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable UUID id, Authentication authentication) {
        UUID viewerUserId = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            String firebaseUid = authentication.getPrincipal().toString();
            viewerUserId = userService.getUserByFirebaseUid(firebaseUid).map(User::getId).orElse(null);
        }

        Signal signal = signalService.getSignalByIdSafe(id);
        if (signal != null) {
            signalService.trackView(id, viewerUserId);
            return ResponseEntity.ok(signal);
        }

        NearbySpace space = signalService.getNearbySpaceById(id);
        if (space != null) {
            signalService.trackView(id, viewerUserId);
            Map<String, Object> spaceMap = new java.util.LinkedHashMap<>();
            spaceMap.put("id", space.getId());
            spaceMap.put("type", "SPACE");
            spaceMap.put("title", space.getName()); 
            spaceMap.put("description", space.getDescription());
            spaceMap.put("category", space.getType());
            spaceMap.put("username", space.getUsername());
            spaceMap.put("userId", space.getUserId());
            spaceMap.put("userName", space.getUserFullName());
            spaceMap.put("avatarUrl", space.getAvatarUrl());
            spaceMap.put("userRole", space.getUserRole());
            spaceMap.put("address", space.getAddress());
            spaceMap.put("city", space.getCity());
            spaceMap.put("state", space.getState());
            spaceMap.put("viewCount", space.getViewCount());
            spaceMap.put("responseCount", space.getResponseCount());
            spaceMap.put("createdAt", space.getCreatedAt());
            spaceMap.put("signalStrength", "normal");
            return ResponseEntity.ok(spaceMap);
        }

        return ResponseEntity.status(404).body(Map.of("error", "Signal not found"));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyData(
            Authentication authentication,
            @RequestParam(required = false) String category) {

        if (authentication == null || authentication.getPrincipal() == null)
            return ResponseEntity.status(401).build();

        String firebaseUid = authentication.getPrincipal().toString();

        return userService.getUserByFirebaseUid(firebaseUid)
                .map(user -> {
                    List<Signal> signals = (category != null)
                            ? signalService.getSignalsByUserAndCategory(user.getId(), category.toLowerCase())
                            : signalService.getSignalsByUser(user.getId());
                    List<NearbySpace> spaces = signalService.getSpacesByUser(user.getId());

                    return ResponseEntity.ok(Map.of("signals", signals, "spaces", spaces));
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSignal(
            Authentication authentication,
            @PathVariable UUID id,
            @RequestBody Signal updatedSignal) {

        if (authentication == null || authentication.getPrincipal() == null)
            return ResponseEntity.status(401).build();

        String firebaseUid = authentication.getPrincipal().toString();

        return userService.getUserByFirebaseUid(firebaseUid)
                .map(user -> {
                    try {
                        Object updated = signalService.updatePost(id, user, updatedSignal);
                        if (updated instanceof Signal) {
                            webSocketService.send("/topic/signals", Map.of("type", "UPDATE", "data", updated));
                        } else {
                            webSocketService.send("/topic/spaces", updated);
                        }
                        return ResponseEntity.ok(updated);
                    } catch (RuntimeException ex) {
                        return ResponseEntity.status(403).body(ex.getMessage());
                    }
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(Authentication authentication, @PathVariable UUID id) {
        if (authentication == null || authentication.getPrincipal() == null)
            return ResponseEntity.status(401).build();

        String firebaseUid = authentication.getPrincipal().toString();

        return userService.getUserByFirebaseUid(firebaseUid)
                .map(user -> {
                    try {
                        String result = signalService.deletePost(id, user);
                        if (result.startsWith("Signal")) {
                            webSocketService.send("/topic/signals", Map.of("type", "DELETE", "signalId", id));
                        } else {
                            webSocketService.send("/topic/spaces", Map.of("type", "DELETE", "spaceId", id));
                        }
                        return ResponseEntity.ok(result);
                    } catch (RuntimeException ex) {
                        return ResponseEntity.status(403).body(ex.getMessage());
                    }
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @GetMapping("/{id}/insights")
    public ResponseEntity<?> getInsights(Authentication authentication, @PathVariable UUID id) {
        if (authentication == null || authentication.getPrincipal() == null)
            return ResponseEntity.status(401).build();

        String firebaseUid = authentication.getPrincipal().toString();

        return userService.getUserByFirebaseUid(firebaseUid)
                .map(user -> {
                    Signal signal = signalService.getSignalById(id);
                    if (!signal.getUserId().equals(user.getId())) {
                        return ResponseEntity.status(403).body("Forbidden");
                    }
                    return ResponseEntity.ok(signalService.getInsights(id));
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/spaces")
    public ResponseEntity<?> createNearbySpace(
            Authentication authentication,
            @RequestBody NearbySpace nearbySpace) {

        if (authentication == null || authentication.getPrincipal() == null)
            return ResponseEntity.status(401).build();

        String firebaseUid = authentication.getPrincipal().toString();

        return userService.getUserByFirebaseUid(firebaseUid)
                .map(user -> {
                    try {
                        signalService.validateSignalCreation(user);
                        nearbySpace.setUser(user);
                        NearbySpace created = signalService.createNearbySpace(user, nearbySpace);
                        webSocketService.send("/topic/spaces", created);
                        return ResponseEntity.ok(created);
                    } catch (IllegalArgumentException ex) {
                        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
                    }
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @GetMapping("/spaces")
    public ResponseEntity<List<NearbySpace>> getNearbySpaces(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false, defaultValue = "10") double radiusKm,
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(signalService.getNearbySpaces(lat, lng, radiusKm, role));
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        return ResponseEntity.ok(signalService.getAllCities());
    }
}
