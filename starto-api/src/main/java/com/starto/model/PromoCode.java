package com.starto.model;

import com.starto.enums.PromoCodeStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "promo_codes",
    indexes = {
        @Index(name = "idx_promo_codes_code", columnList = "code"),
        @Index(name = "idx_promo_codes_status", columnList = "status")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Builder.Default
    @Column(nullable = false)
    private int discount = 100;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PromoCodeStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "used_by_id")
    private User usedBy;

    @Column(name = "used_at")
    private OffsetDateTime usedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
