package com.starto.service;

import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.starto.config.PlanConfig;
import com.starto.dto.SubscriptionResponseDTO;
import com.starto.enums.BillingType;
import com.starto.enums.Plan;
import com.starto.model.PlanEntity;
import com.starto.model.Subscription;
import com.starto.model.User;
import com.starto.repository.PlanRepository;
import com.starto.repository.SubscriptionRepository;
import com.starto.repository.UserRepository;
import com.starto.repository.SignalRepository;
import com.starto.repository.NearbySpaceRepository;
import com.starto.repository.OfferRepository;
import com.starto.repository.AiUsageRepository;
import lombok.RequiredArgsConstructor;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import com.starto.service.EmailService;
import com.starto.service.NotificationService;
import com.razorpay.Payment;

import jakarta.transaction.Transactional;

import com.starto.repository.PlanRepository;
import java.util.Map;
import java.util.List;
import java.util.UUID;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final RazorpayService razorpayService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final PlanRepository planRepository;
    private final SignalRepository signalRepository;
    private final NearbySpaceRepository nearbySpaceRepository;
    private final OfferRepository offerRepository;
    private final AiUsageRepository aiUsageRepository;

    public SubscriptionResponseDTO createOrder(User user, String plan) {

    Plan planEnum = Plan.fromString(plan);

    PlanEntity planEntity = planRepository.findByCode(planEnum)
            .orElseThrow(() -> new RuntimeException("Plan not found"));

    int amountPaise = planEntity.getPricePaise();

    String orderId = null;
    String subscriptionId = null;

    if (planEntity.getBillingType() == BillingType.ONE_TIME) {

        //  ONE-TIME PAYMENT
        orderId = razorpayService.createOrder(amountPaise);

    } else {

        //  RECURRING PAYMENT
        if (planEntity.getRazorpayPlanId() == null) {
            throw new RuntimeException("Razorpay plan ID not configured for this plan");
        }

        subscriptionId = razorpayService.createSubscription(
                planEntity.getRazorpayPlanId()
        );
    }

    //  SAVE SUBSCRIPTION
    Subscription subscription = Subscription.builder()
            .user(user)
            .plan(planEnum.name())
            .amountPaid(amountPaise)
            .razorpayOrderId(orderId) // null for recurring
            .razorpaySubscriptionId(subscriptionId) // null for one-time
            .status("PENDING")
            .createdAt(OffsetDateTime.now())
            .build();

    Subscription saved = subscriptionRepository.save(subscription);

    //  RESPONSE TO FRONTEND
    return SubscriptionResponseDTO.builder()
            .id(saved.getId())
            .plan(saved.getPlan())
            .status(saved.getStatus())
            .amountPaid(saved.getAmountPaid())
            .razorpayOrderId(orderId)
            .razorpaySubscriptionId(subscriptionId) 
            .build();
}

    //   `signature` param + corrected variable names + Plan enum lookup
    @Transactional
    public void activateSubscription(String razorpayOrderId, String razorpayPaymentId, String signature) {
        Subscription subscription = subscriptionRepository
                .findByRazorpayOrderId(razorpayOrderId)         
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("ACTIVE".equals(subscription.getStatus())) {
            throw new RuntimeException("Subscription already activated");
        }

        //before deploy uncomment below line
      //  boolean verified = razorpayService.verifySignature(razorpayOrderId, razorpayPaymentId, signature); 
      boolean  verified = true;
        if (!verified) throw new RuntimeException("Payment verification failed");

        Plan planEnum = Plan.fromString(subscription.getPlan()); // enum lookup
        PlanEntity planEntity = planRepository.findByCode(planEnum)
        .orElseThrow(() -> new RuntimeException("Plan not found"));

       int days = planEntity.getDurationDays();
        OffsetDateTime now = OffsetDateTime.now();

        subscription.setRazorpayPaymentId(razorpayPaymentId);
        subscription.setStatus("ACTIVE");
        subscription.setStartsAt(now);
        subscription.setExpiresAt(now.plusDays(days));
        subscriptionRepository.save(subscription);

       User user = userRepository.findById(subscription.getUser().getId())
        .orElseThrow(() -> new RuntimeException("User not found"));
    Plan previousPlan = user.getPlan();

    // expire all other active subscriptions — handles upgrade case
    subscriptionRepository.findActiveByUserId(user.getId())
        .forEach(sub -> {
            if (!sub.getId().equals(subscription.getId())) {
                sub.setStatus("SUPERSEDED");
                subscriptionRepository.save(sub);
            }
        });

    // update user plan
    user.setPlan(planEnum);
    user.setPlanExpiresAt(subscription.getExpiresAt());
    userRepository.saveAndFlush(user);

    // send payment receipt
    emailService.sendPaymentSuccessEmail(
        user,
        subscription.getPlan(),
        subscription.getAmountPaid(),
        razorpayOrderId
    );

    // send upgrade or welcome email
    if (previousPlan != Plan.EXPLORER && previousPlan != planEnum) {
        System.out.println(" About to send subscription email to: "+user.getEmail());
        emailService.sendPlanUpgradeEmail(user, previousPlan.name(), planEnum.name());
        System.out.println(" Email method called");
    } else {
        emailService.sendWelcomePlanEmail(user);
    }

    // in-app notification
    notificationService.send(
        user.getId(),
        "PAYMENT_SUCCESS",
        "Payment Successful!",
        "Your " + subscription.getPlan() + " plan is now active.",
        Map.of(
            "plan", subscription.getPlan(),
            "expiresAt", subscription.getExpiresAt().toString(),
            "amountPaid", subscription.getAmountPaid()
        )
    );
    }

    public boolean isPlanActive(User user) {
        if (user.getPlan().name().equalsIgnoreCase("EXPLORER")) return true;
        return user.getPlanExpiresAt() != null &&
               user.getPlanExpiresAt().isAfter(OffsetDateTime.now());
    }

    public boolean canUnlockWhatsapp(User user) {
        Plan planEnum = user.getPlan();
        return isPlanActive(user) &&
               PlanConfig.WHATSAPP_UNLOCK.getOrDefault(planEnum, false);
    }

    public int getMaxSignals(User user) {
        Plan planEnum = user.getPlan();
        return PlanConfig.MAX_SIGNALS.getOrDefault(planEnum, 2);
    }

    public int getMaxOffers(User user) {
        Plan planEnum = user.getPlan();
        return PlanConfig.MAX_OFFERS.getOrDefault(planEnum, 3);
    }

    public List<SubscriptionResponseDTO> getPaymentHistory(UUID userId) {
    return subscriptionRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(sub -> SubscriptionResponseDTO.builder()
                    .id(sub.getId())
                    .plan(sub.getPlan())
                    .status(sub.getStatus())
                    .amountPaid(sub.getAmountPaid())
                    .razorpayOrderId(sub.getRazorpayOrderId())
                    .startsAt(sub.getStartsAt())
                    .expiresAt(sub.getExpiresAt())
                    .build()
            )
            .collect(java.util.stream.Collectors.toList());
}


public SubscriptionResponseDTO upgradePlan(User user, String newPlan) {

    Plan newPlanEnum = Plan.fromString(newPlan);
    Plan currentPlan = user.getPlan();

    // same plan check
    if (currentPlan == newPlanEnum) {
        throw new RuntimeException("You are already on " + newPlan + " plan");
    }

    // cant switch to free
    if (newPlanEnum == Plan.EXPLORER) {
        throw new RuntimeException("Cannot switch to free plan manually");
    }

    // check plan hierarchy — prevent downgrade
    int currentPlanPrice = PlanConfig.PLAN_PRICE_PAISE.getOrDefault(currentPlan, 0);
    int newPlanPrice = PlanConfig.PLAN_PRICE_PAISE.getOrDefault(newPlanEnum, 0);

    if (newPlanPrice < currentPlanPrice) {
        throw new RuntimeException(
            "Cannot downgrade from " + currentPlan.name() + 
            " to " + newPlan + ". Please wait for your current plan to expire."
        );
    }

    // check if current plan is active
    boolean isCurrentActive = user.getPlanExpiresAt() != null &&
            user.getPlanExpiresAt().isAfter(OffsetDateTime.now());

    if (isCurrentActive) {
        // calculate remaining days on current plan
        long remainingDays = java.time.temporal.ChronoUnit.DAYS.between(
            OffsetDateTime.now(), user.getPlanExpiresAt()
        );
        System.out.println("User has " + remainingDays + " days remaining on " + currentPlan.name());
        // note: remaining days are forfeited on upgrade
        // you can add credit logic here later if needed
    }

    // create one-time order for new plan
    int amountPaise = PlanConfig.PLAN_PRICE_PAISE.get(newPlanEnum);
    String orderId = razorpayService.createOrder(amountPaise);

    com.starto.model.Subscription subscription = com.starto.model.Subscription.builder()
            .user(user)
            .plan(newPlanEnum.name())
            .amountPaid(amountPaise)
            .razorpayOrderId(orderId)
            .status("PENDING")
            .createdAt(OffsetDateTime.now())
            .build();

    com.starto.model.Subscription saved = subscriptionRepository.save(subscription);

    return SubscriptionResponseDTO.builder()
            .id(saved.getId())
            .plan(saved.getPlan())
            .status(saved.getStatus())
            .amountPaid(saved.getAmountPaid())
            .razorpayOrderId(saved.getRazorpayOrderId())
            .build();
}



    public Map<String, Object> getCurrentPlanStatus(User user) {
        OffsetDateTime now = OffsetDateTime.now();
        Plan plan = user.getPlan();
        boolean isActive = user.getPlanExpiresAt() == null || user.getPlanExpiresAt().isAfter(now);
        long daysLeft = user.getPlanExpiresAt() == null ? 0 : java.time.temporal.ChronoUnit.DAYS.between(now, user.getPlanExpiresAt());

        // Determine plan start date
        OffsetDateTime planStart = user.getPlanExpiresAt() != null 
            ? user.getPlanExpiresAt().minusDays(PlanConfig.PLAN_DURATION_DAYS.getOrDefault(plan, 30))
            : user.getCreatedAt();

        // Signal Usage (Active signals + spaces)
        int signalLimit = PlanConfig.MAX_SIGNALS.getOrDefault(plan, 0);
        long signalsUsed = signalRepository.countByUserIdAndCreatedAtAfter(user.getId(), planStart) +
                          nearbySpaceRepository.countByUser_IdAndCreatedAtAfter(user.getId(), planStart);
        long signalsLeft = signalLimit == Integer.MAX_VALUE ? 9999 : Math.max(0, signalLimit - signalsUsed);

        // Offer Usage (Since plan start)
        int offerLimit = PlanConfig.MAX_OFFERS.getOrDefault(plan, 0);
        long offersUsed = offerRepository.countByRequesterIdAndCreatedAtAfter(user.getId(), planStart);
        long offersLeft = offerLimit == Integer.MAX_VALUE ? 9999 : Math.max(0, offerLimit - offersUsed);

        // AI Usage (Since plan start)
        int aiLimit = PlanConfig.MAX_AI_CALLS.getOrDefault(plan, 0);
        long aiUsed = aiUsageRepository.countByUserIdAndDateAfter(user.getId(), planStart.toLocalDate());
        long aiLeft = aiLimit == Integer.MAX_VALUE ? 9999 : Math.max(0, aiLimit - aiUsed);

        Map<String, Object> status = new java.util.HashMap<>();
        status.put("plan", plan.name());
        status.put("isActive", isActive);
        status.put("daysLeft", Math.max(daysLeft, 0));
        status.put("expiresAt", user.getPlanExpiresAt());
        status.put("signalsLeft", signalsLeft);
        status.put("offersLeft", offersLeft);
        status.put("aiLeft", aiLeft);
        status.put("isVerified", user.getIsVerified());
        
        return status;
    }
@Transactional
public void activateSubscriptionBySubscription(String subscriptionId, String paymentId) {

    Subscription subscription = subscriptionRepository
            .findByRazorpaySubscriptionId(subscriptionId)
            .orElseThrow(() -> new RuntimeException("Subscription not found"));

    if ("ACTIVE".equals(subscription.getStatus())) {
        throw new RuntimeException("Already active");
    }

  
    JSONObject payment = razorpayService.fetchPayment(paymentId);

    String fetchedSubId = payment.optString("subscription_id");

     if (!subscriptionId.equals(fetchedSubId)) {
         throw new RuntimeException("Invalid subscription-payment mapping");
     }

    subscription.setStatus("ACTIVE");
    subscription.setRazorpayPaymentId(paymentId);

    OffsetDateTime now = OffsetDateTime.now();

    Plan planEnum = Plan.fromString(subscription.getPlan());
    PlanEntity planEntity = planRepository.findByCode(planEnum)
            .orElseThrow(() -> new RuntimeException("Plan not found"));

    subscription.setStartsAt(now);
    subscription.setExpiresAt(now.plusDays(planEntity.getDurationDays()));

    subscriptionRepository.save(subscription);

    User user = subscription.getUser();
    user.setPlan(planEnum);
    user.setPlanExpiresAt(subscription.getExpiresAt());
    userRepository.save(user);

    emailService.sendPaymentSuccessEmail(
    user,
    subscription.getPlan(),
    subscription.getAmountPaid(),
    subscriptionId
);
}
}