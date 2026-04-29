package com.starto.controller;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import com.starto.dto.ConnectionRequestDTO;
import com.starto.dto.ConnectionResponseDTO;
import com.starto.model.Connection;
import com.starto.model.User;
import com.starto.enums.Plan;
import com.starto.service.ConnectionService;
import com.starto.service.PlanService;
import com.starto.service.UserService;
import com.starto.service.WebSocketService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;
    private final UserService userService;
    private final WebSocketService webSocketService;
    private final PlanService planService;

    // 🔹 Helper method
    private User getUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        return userService
                .getUserByFirebaseUid(authentication.getPrincipal().toString())
                .orElse(null);
    }

    // SEND REQUEST
    @PostMapping("/request")
    public ResponseEntity<?> sendRequest(
            Authentication authentication,
            @RequestBody ConnectionRequestDTO dto) {

        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        try {
            Connection connection = connectionService.sendRequest(
                    user,
                    dto.getReceiverId(),
                    dto.getSignalId(),
                    dto.getSpaceId(),
                    dto.getMessage()
            );

            webSocketService.send(
                    "/topic/connections/" + connection.getReceiver().getId(),
                    Map.of("type", "NEW_REQUEST", "data", connection)
            );

            return ResponseEntity.ok(ConnectionResponseDTO.from(connection));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ACCEPT REQUEST
    @PutMapping("/{connectionId}/accept")
    public ResponseEntity<?> acceptRequest(
            Authentication authentication,
            @PathVariable UUID connectionId) {

        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Connection updated = connectionService.acceptRequest(user, connectionId);

        webSocketService.send(
                "/topic/connections/" + updated.getRequester().getId(),
                Map.of("type", "ACCEPT", "data", updated)
        );

        return ResponseEntity.ok(ConnectionResponseDTO.from(updated));
    }

    // REJECT REQUEST
    @PutMapping("/{connectionId}/reject")
    public ResponseEntity<?> rejectRequest(
            Authentication authentication,
            @PathVariable UUID connectionId) {

        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Connection updated = connectionService.rejectRequest(user, connectionId);

        webSocketService.send(
                "/topic/connections/" + updated.getRequester().getId(),
                Map.of("type", "REJECT", "data", updated)
        );

        return ResponseEntity.ok(ConnectionResponseDTO.from(updated));
    }

    // PENDING
    @Transactional(readOnly = true)
    @GetMapping("/pending")
    public ResponseEntity<List<ConnectionResponseDTO>> getPending(Authentication authentication) {
        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(
                connectionService.getPendingRequests(user.getId())
                        .stream()
                        .map(ConnectionResponseDTO::from)
                        .toList()
        );
    }

    // SENT
    @Transactional(readOnly = true)
    @GetMapping("/sent")
    public ResponseEntity<List<ConnectionResponseDTO>> getSent(Authentication authentication) {
        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(
                connectionService.getSentRequests(user.getId())
                        .stream()
                        .map(ConnectionResponseDTO::from)
                        .toList()
        );
    }

    // ACCEPTED (Current User)
    @Transactional(readOnly = true)
    @GetMapping("/accepted")
    public ResponseEntity<List<ConnectionResponseDTO>> getAccepted(Authentication authentication) {
        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(
                connectionService.getAcceptedConnections(user.getId())
                        .stream()
                        .map(ConnectionResponseDTO::from)
                        .toList()
        );
    }

    // ACCEPTED FOR SPECIFIC USER
    @Transactional(readOnly = true)
    @GetMapping("/user/{userId}/accepted")
    public ResponseEntity<List<ConnectionResponseDTO>> getAcceptedForUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(
                connectionService.getAcceptedConnections(userId)
                        .stream()
                        .map(ConnectionResponseDTO::from)
                        .toList()
        );
    }

    //  WHATSAPP LINK (MONETIZATION POINT)
    //  WhatsApp link with PLAN CONTROL
    @Transactional(readOnly = true)
    @GetMapping("/{connectionId}/whatsapp")
    public ResponseEntity<?> getWhatsappLink(
            Authentication authentication,
            @PathVariable UUID connectionId) {

        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();
        Plan plan = user.getPlan();

        if (!planService.hasWhatsappAccess(plan)) {
            return ResponseEntity.status(403).body(
                    Map.of("error", "Upgrade your plan to unlock WhatsApp contact")
            );
        }

        Connection connection = connectionService.getConnectionById(connectionId);

        // Security check
        if (!connection.getRequester().getId().equals(user.getId()) &&
            !connection.getReceiver().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
        }

        String link = connectionService.getWhatsappLink(user, connectionId);

        return ResponseEntity.ok(Map.of("whatsappUrl", link));
    }
}