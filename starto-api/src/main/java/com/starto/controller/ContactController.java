package com.starto.controller;

import com.starto.service.EmailService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContactController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<?> sendContactInquiry(@RequestBody ContactRequest request) {
        try {
            emailService.sendContactEmail(request.getName(), request.getEmail(), request.getMessage());
            return ResponseEntity.ok(Map.of("message", "Inquiry sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send inquiry: " + e.getMessage()));
        }
    }

    @Data
    public static class ContactRequest {
        private String name;
        private String email;
        private String message;
    }
}
