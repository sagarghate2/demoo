package com.starto.repository;

import com.starto.model.PromoCode;
import com.starto.enums.PromoCodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromoCodeRepository extends JpaRepository<PromoCode, UUID> {

    Optional<PromoCode> findByCode(String code);

    List<PromoCode> findByStatus(PromoCodeStatus status);

    long countByStatus(PromoCodeStatus status);

    @Query("SELECT p FROM PromoCode p WHERE (:status IS NULL OR p.status = :status) AND (:search IS NULL OR p.code LIKE %:search%)")
    List<PromoCode> findAllByStatusAndSearch(@Param("status") PromoCodeStatus status, @Param("search") String search);
}
