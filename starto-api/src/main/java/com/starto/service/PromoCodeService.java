package com.starto.service;

import com.starto.enums.PromoCodeStatus;
import com.starto.model.PromoCode;
import com.starto.model.User;
import com.starto.repository.PromoCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;

    @Transactional
    public List<PromoCode> generatePromoCodes(int count) {
        List<PromoCode> generatedCodes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String code = generateUniqueCode();
            PromoCode promoCode = PromoCode.builder()
                    .code(code)
                    .discount(100)
                    .status(PromoCodeStatus.UNUSED)
                    .build();
            generatedCodes.add(promoCodeRepository.save(promoCode));
        }
        return generatedCodes;
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = "BETA-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (promoCodeRepository.findByCode(code).isPresent());
        return code;
    }

    public List<PromoCode> listCodes(PromoCodeStatus status, String search) {
        if (status != null) {
            return promoCodeRepository.findAllByStatusAndSearch(status, search);
        } else {
            return promoCodeRepository.findAllByStatusAndSearch(null, search);
        }
    }

    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", promoCodeRepository.count());
        stats.put("used", promoCodeRepository.countByStatus(PromoCodeStatus.USED));
        stats.put("unused", promoCodeRepository.countByStatus(PromoCodeStatus.UNUSED));
        stats.put("expired", promoCodeRepository.countByStatus(PromoCodeStatus.EXPIRED));
        return stats;
    }

    @Transactional
    public PromoCode updateStatus(UUID id, PromoCodeStatus status) {
        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promo code not found"));
        promoCode.setStatus(status);
        return promoCodeRepository.save(promoCode);
    }

    @Transactional
    public void deleteCode(UUID id) {
        PromoCode promoCode = promoCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promo code not found"));
        if (promoCode.getStatus() != PromoCodeStatus.UNUSED) {
            throw new RuntimeException("Only unused promo codes can be deleted");
        }
        promoCodeRepository.delete(promoCode);
    }

    @Transactional
    public void redeemCode(User user, String code) {
        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid promo code"));

        if (promoCode.getStatus() != PromoCodeStatus.UNUSED) {
            throw new RuntimeException("Promo code has already been used or is inactive");
        }

        promoCode.setStatus(PromoCodeStatus.USED);
        promoCode.setUsedBy(user);
        promoCode.setUsedAt(OffsetDateTime.now());
        promoCodeRepository.save(promoCode);
    }
}
