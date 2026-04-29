package com.starto.repository;

import com.starto.model.NearbySpace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
import java.time.OffsetDateTime;

public interface NearbySpaceRepository extends JpaRepository<NearbySpace, UUID> {
    List<NearbySpace> findByCity(String city);

    List<NearbySpace> findByUser_Id(UUID userId);
    long countByUser_Id(UUID userId);
    long countByUser_IdAndCreatedAtAfter(UUID userId, OffsetDateTime timestamp);

    // bounding-box search by geo coords
    List<NearbySpace> findByLatBetweenAndLngBetween(java.math.BigDecimal latMin, java.math.BigDecimal latMax, java.math.BigDecimal lngMin, java.math.BigDecimal lngMax);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM NearbySpace s JOIN FETCH s.user ORDER BY s.createdAt DESC")
    List<NearbySpace> findAllWithUser();

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query(value = "UPDATE nearby_spaces s SET response_count = (SELECT COUNT(*) FROM comments c WHERE c.space_id = s.id)", nativeQuery = true)
    void syncAllResponseCounts();
}
