package com.starto.controller;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import com.starto.dto.OfferRequestDTO;
import com.starto.dto.OfferResponseDTO;
import com.starto.model.Offer;
import com.starto.model.User;
import com.starto.enums.Plan;
import com.starto.service.OfferService;
import com.starto.service.PlanService;
import com.starto.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;
    private final UserService userService;
    private final PlanService planService;

    // 🔹 helper
    private User getUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        return userService
                .getUserByFirebaseUid(authentication.getPrincipal().toString())
                .orElse(null);
    }

    // talent sends offer
   @PostMapping("/request")
public ResponseEntity<?> sendOffer(
        Authentication authentication,
        @RequestBody OfferRequestDTO dto) {

    User user = getUser(authentication);
    if (user == null) return ResponseEntity.status(401).build();

    //  Use safe parser (VERY IMPORTANT)
    Plan plan = user.getPlan();

    //  Get current usage
    java.time.OffsetDateTime planStart = user.getPlanExpiresAt() != null 
        ? user.getPlanExpiresAt().minusDays(com.starto.config.PlanConfig.PLAN_DURATION_DAYS.getOrDefault(plan, 30))
        : user.getCreatedAt();

    long usedOffers = offerService.countUserOffers(user.getId(), planStart);

    //  Enforce limit
    if (!planService.canSendOffer(plan, usedOffers)) {
        return ResponseEntity.status(403).body(Map.of(
                "error", "Offer limit reached",
                "upgradeUrl", "/api/subscriptions/upgrade"
        ));
    }

    return ResponseEntity.ok(offerService.sendOffer(user, dto));
}

    // founder sees inbox
    @Transactional(readOnly = true)
    @GetMapping("/inbox")
    public ResponseEntity<List<OfferResponseDTO>> getInbox(Authentication authentication) {
        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(
                offerService.getAllOffers(user.getId())
                        .stream()
                        .map(OfferResponseDTO::from)
                        .toList()
        );
    }

    // talent sees sent
    @Transactional(readOnly = true)
    @GetMapping("/sent")
    public ResponseEntity<List<OfferResponseDTO>> getSent(Authentication authentication) {
        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(
                offerService.getSentOffers(user.getId())
                        .stream()
                        .map(OfferResponseDTO::from)
                        .toList()
        );
    }

    // accept
    @PostMapping("/{offerId}/accept")
    public ResponseEntity<OfferResponseDTO> acceptOffer(
            Authentication authentication,
            @PathVariable UUID offerId) {
        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(OfferResponseDTO.from(offerService.acceptOffer(user, offerId)));
    }

    // reject
    @PostMapping("/{offerId}/reject")
    public ResponseEntity<OfferResponseDTO> rejectOffer(
            Authentication authentication,
            @PathVariable UUID offerId) {
        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(OfferResponseDTO.from(offerService.rejectOffer(user, offerId)));
    }

    // delete
    @DeleteMapping("/{offerId}")
    public ResponseEntity<Void> deleteOffer(
            Authentication authentication,
            @PathVariable UUID offerId) {
        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        offerService.deleteOffer(user, offerId);
        return ResponseEntity.noContent().build();
    }

    //  WhatsApp link with PLAN CONTROL
    @Transactional(readOnly = true)
    @GetMapping("/{offerId}/whatsapp")
    public ResponseEntity<?> getWhatsappLink(
            Authentication authentication,
            @PathVariable UUID offerId) {

        User user = getUser(authentication);
        if (user == null) return ResponseEntity.status(401).build();

        Offer offer = offerService.getOfferById(offerId);

        //  Convert plan
        Plan plan = user.getPlan();

        //  Founder always allowed
        if (offer.getReceiverId().equals(user.getId())) {
            String link = offerService.getWhatsappLink(user, offerId);
            return ResponseEntity.ok(Map.of("whatsappUrl", link));
        }

        //  Talent (plan-based access)
        if (offer.getRequesterId().equals(user.getId())) {

            if (planService.isWhatsappUnlocked(plan)) {
                String link = offerService.getWhatsappLink(user, offerId);
                return ResponseEntity.ok(Map.of("whatsappUrl", link));
            } else {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "Upgrade your plan to unlock WhatsApp",
                        "upgradeUrl", "/api/subscriptions/upgrade"
                ));
            }
        }

        return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));
    }
}