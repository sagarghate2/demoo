package com.starto.service;

import com.starto.enums.Plan;
import com.starto.model.User;
import com.starto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;
import com.starto.service.NotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.starto.service.EmailService;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;


 public Optional<User> getUserByFirebaseUid(String firebaseUid) {
    Optional<User> userOpt = userRepository.findByFirebaseUid(firebaseUid);
    
    System.out.println("USER FOUND: " + userOpt.isPresent());
    System.out.println("FIREBASE UID SEARCHED: '" + firebaseUid + "'");
    
    userOpt.ifPresent(user -> {

        if (user.getPlan() != Plan.EXPLORER
            && user.getPlanExpiresAt() != null
            && user.getPlanExpiresAt().isBefore(OffsetDateTime.now())) {

        String expiredPlanName = user.getPlan().name();  // ← save before changing

        user.setPlan(Plan.EXPLORER);
        user.setPlanExpiresAt(null);
        userRepository.save(user);

        // notify with correct plan name
        notificationService.send(
            user.getId(),
            "PLAN_EXPIRED",
            "Plan Expired",
            "Your " + expiredPlanName + " plan has expired. Upgrade to continue.",
            Map.of("plan", "EXPLORER")
        );

        System.out.println("PLAN EXPIRED - downgraded to EXPLORER");
    }
        
        System.out.println("USER EMAIL: " + user.getEmail());
        System.out.println("USER PLAN: " + user.getPlan());
    });
    
    System.out.println("RETURNING: " + userOpt.isPresent());
    return userOpt;
}

@Transactional
public void syncVerificationAndSendWelcome(User user) {
    if (user.getWelcomeEmailSent() != null && user.getWelcomeEmailSent()) {
        return; // Already sent
    }

    try {
        UserRecord userRecord = FirebaseAuth.getInstance().getUser(user.getFirebaseUid());
        if (userRecord.isEmailVerified()) {
            user.setIsVerified(true);
            user.setWelcomeEmailSent(true);
            userRepository.save(user);
            emailService.sendWelcomeEmail(user);
            System.out.println("WELCOME EMAIL SENT TO VERIFIED USER: " + user.getEmail());
        }
    } catch (Exception e) {
        System.err.println("FAILED TO SYNC VERIFICATION FOR: " + user.getEmail() + " | " + e.getMessage());
    }
}


public User getUserCached(String firebaseUid) {
    return userRepository.findByFirebaseUid(firebaseUid).orElse(null);
}


    @Transactional
public User createOrUpdateUser(String firebaseUid,
                                 String email,
                                 String name,
                                 String phone,
                                 String role,
                                 String city,
                                 String state,
                                 String country,
                                 String gender,
                                 String bio,
                                 String avatarUrl,
                                 BigDecimal lat,
                                 BigDecimal lng,
                                 String address) {

    return userRepository.findByFirebaseUid(firebaseUid)
            .map(user -> {

                user.setLastSeen(OffsetDateTime.now());
                user.setIsOnline(true);

                // only fill missing fields (DO NOT overwrite existing data)
                if (user.getCity() == null) user.setCity(city);
                if (user.getState() == null) user.setState(state);
                if (user.getCountry() == null) user.setCountry(country != null ? country : "India");
                if (user.getPhone() == null) user.setPhone(phone);
                if (user.getGender() == null) user.setGender(gender);
                if (user.getBio() == null) user.setBio(bio);
                if (user.getAvatarUrl() == null) user.setAvatarUrl(avatarUrl);
                if (user.getLat() == null) user.setLat(lat);
                if (user.getLng() == null) user.setLng(lng);
                if (user.getAddress() == null) user.setAddress(address);

                return userRepository.save(user);
            })
            .orElseGet(() -> {

                String finalUsername = generateUniqueUsername(name, role);

                User newUser = User.builder()
                        .firebaseUid(firebaseUid)
                        .email(email)
                        .name(name)
                        .phone(phone)
                        .role(role)
                        .city(city)
                        .state(state)
                        .country(country != null ? country : "India")
                        .gender(gender)
                        .bio(bio)
                        .avatarUrl(avatarUrl)
                        .lat(lat)
                        .lng(lng)
                        .address(address)
                        .username(finalUsername)
                        .plan(Plan.EXPLORER)
                        .planPurchasedAt(OffsetDateTime.now())
                        .isOnline(true)
                        .lastSeen(OffsetDateTime.now())
                        .build();

                return userRepository.save(newUser);
            });
}

    @Transactional
public User updateProfile(User user) {

    User existing = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

   // UserService.updateProfile — add missing fields
if (user.getUsername() != null) existing.setUsername(user.getUsername());
if (user.getSubIndustry() != null) existing.setSubIndustry(user.getSubIndustry());
if (user.getWebsiteUrl() != null) existing.setWebsiteUrl(user.getWebsiteUrl());
if (user.getLinkedinUrl() != null) existing.setLinkedinUrl(user.getLinkedinUrl());
if (user.getTwitterUrl() != null) existing.setTwitterUrl(user.getTwitterUrl());
if (user.getGithubUrl() != null) existing.setGithubUrl(user.getGithubUrl());
if (user.getLat() != null) existing.setLat(user.getLat());
if (user.getLng() != null) existing.setLng(user.getLng());
if (user.getAddress() != null) existing.setAddress(user.getAddress());
if (user.getIndustry() != null) existing.setIndustry(user.getIndustry());
if (user.getGender() != null) existing.setGender(user.getGender());
if (user.getAvatarUrl() != null) existing.setAvatarUrl(user.getAvatarUrl());

    existing.setUpdatedAt(OffsetDateTime.now());

    return userRepository.save(existing);
}

   public boolean isUsernameAvailable(String baseUsername, String role) { ///////
    String finalUsername = baseUsername + "_" + role.toLowerCase();
    return !userRepository.existsByUsername(finalUsername);
}


    public Optional<User> getUserByUsername(String username) {
        System.out.println("[UserLookup] Searching for identifier: '" + username + "'");
        // Smart Lookup: check if it's a UUID first
        try {
            java.util.UUID id = java.util.UUID.fromString(username);
            System.out.println("[UserLookup] Detected UUID format. Fetching by ID...");
            Optional<User> result = userRepository.findById(id);
            System.out.println("[UserLookup] UUID Result Present: " + result.isPresent());
            return result;
        } catch (IllegalArgumentException e) {
            // Not a UUID, proceed with username lookup
            System.out.println("[UserLookup] Detected Username format. Fetching by handle...");
            Optional<User> result = userRepository.findByUsername(username);
            System.out.println("[UserLookup] Username Result Present: " + result.isPresent());
            return result;
        }
    }

private String generateUniqueUsername(String name, String role) {

    String base = name.toLowerCase().trim().replaceAll("\\s+", "");
    String baseUsername = base + "_" + role.toLowerCase();

    String finalUsername = baseUsername;
    int i = 1;

    while (userRepository.existsByUsername(finalUsername)) {
        finalUsername = baseUsername + i;
        i++;
    }

    return finalUsername;
}


@Transactional
public void updatePresence(String firebaseUid) {
    userRepository.findByFirebaseUid(firebaseUid).ifPresent(user -> {
        user.setIsOnline(true);
        user.setLastSeen(OffsetDateTime.now());
        userRepository.save(user);
    });
}

    @Transactional
    public void markOffline(String firebaseUid) {
        userRepository.findByFirebaseUid(firebaseUid).ifPresent(user -> {
            user.setIsOnline(false);
            user.setLastSeen(OffsetDateTime.now());
            userRepository.save(user);
        });
    }




}
