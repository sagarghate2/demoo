package com.starto.controller;

import com.starto.model.User;
import com.starto.service.UserService;
import com.starto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.starto.service.PromoCodeService;
import com.starto.enums.PromoCodeStatus;
import com.starto.model.PromoCode;
import java.util.UUID;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PromoCodeService promoCodeService;

    private static final String ADMIN_EMAIL = "krishnamurthikm07@gmail.com";

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String firebaseUid = authentication.getPrincipal().toString();
        User currentUser = userService.getUserByFirebaseUid(firebaseUid).orElse(null);

        if (currentUser == null || !ADMIN_EMAIL.equalsIgnoreCase(currentUser.getEmail())) {
            return ResponseEntity.status(403).body("Forbidden: Only designated admin can access this dashboard.");
        }

        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        String firebaseUid = authentication.getPrincipal().toString();
        User currentUser = userService.getUserByFirebaseUid(firebaseUid).orElse(null);

        if (currentUser == null || !ADMIN_EMAIL.equalsIgnoreCase(currentUser.getEmail())) {
            return ResponseEntity.status(403).build();
        }

        long totalUsers = userRepository.count();
        
        return ResponseEntity.ok(Map.of(
            "totalUsers", totalUsers
        ));
    }

    @GetMapping("/promo-codes")
    public ResponseEntity<?> getPromoCodes(
            Authentication authentication,
            @RequestParam(required = false) PromoCodeStatus status,
            @RequestParam(required = false) String search) {
        
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        String firebaseUid = authentication.getPrincipal().toString();
        User currentUser = userService.getUserByFirebaseUid(firebaseUid).orElse(null);

        if (currentUser == null || !ADMIN_EMAIL.equalsIgnoreCase(currentUser.getEmail())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(promoCodeService.listCodes(status, search));
    }

    @PostMapping("/promo-codes/generate")
    public ResponseEntity<?> generatePromoCodes(
            Authentication authentication,
            @RequestBody Map<String, Integer> body) {
        
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        String firebaseUid = authentication.getPrincipal().toString();
        User currentUser = userService.getUserByFirebaseUid(firebaseUid).orElse(null);

        if (currentUser == null || !ADMIN_EMAIL.equalsIgnoreCase(currentUser.getEmail())) {
            return ResponseEntity.status(403).build();
        }

        int count = body.getOrDefault("count", 0);
        if (count <= 0) {
            return ResponseEntity.badRequest().body("Count must be greater than 0");
        }

        return ResponseEntity.ok(promoCodeService.generatePromoCodes(count));
    }

    @PutMapping("/promo-codes/{id}/status")
    public ResponseEntity<?> updatePromoCodeStatus(
            Authentication authentication,
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        String firebaseUid = authentication.getPrincipal().toString();
        User currentUser = userService.getUserByFirebaseUid(firebaseUid).orElse(null);

        if (currentUser == null || !ADMIN_EMAIL.equalsIgnoreCase(currentUser.getEmail())) {
            return ResponseEntity.status(403).build();
        }

        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().body("Status is required");
        }

        try {
            PromoCodeStatus status = PromoCodeStatus.valueOf(statusStr.toUpperCase());
            return ResponseEntity.ok(promoCodeService.updateStatus(id, status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status");
        }
    }

    @DeleteMapping("/promo-codes/{id}")
    public ResponseEntity<?> deletePromoCode(
            Authentication authentication,
            @PathVariable UUID id) {
        
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        String firebaseUid = authentication.getPrincipal().toString();
        User currentUser = userService.getUserByFirebaseUid(firebaseUid).orElse(null);

        if (currentUser == null || !ADMIN_EMAIL.equalsIgnoreCase(currentUser.getEmail())) {
            return ResponseEntity.status(403).build();
        }

        try {
            promoCodeService.deleteCode(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/promo-codes/stats")
    public ResponseEntity<?> getPromoCodeStats(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        String firebaseUid = authentication.getPrincipal().toString();
        User currentUser = userService.getUserByFirebaseUid(firebaseUid).orElse(null);

        if (currentUser == null || !ADMIN_EMAIL.equalsIgnoreCase(currentUser.getEmail())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(promoCodeService.getStats());
    }
}
