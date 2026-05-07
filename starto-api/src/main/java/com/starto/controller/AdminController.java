package com.starto.controller;

import com.starto.model.User;
import com.starto.service.UserService;
import com.starto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;

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
        // You can add more stats here later
        
        return ResponseEntity.ok(Map.of(
            "totalUsers", totalUsers
        ));
    }
}
