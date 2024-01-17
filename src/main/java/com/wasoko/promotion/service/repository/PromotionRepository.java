package com.wasoko.promotion.service.repository;

import com.wasoko.promotion.service.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The interface Promotion repository.
 */
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    /**
     * Find all by id in list.
     *
     * @param ids the ids
     * @return the list
     */
    List<Promotion> findAllByIdIn(@Param("ids") List<Long> ids);

    /**
     * Find an live or approved promotion by ID within the valid date range.
     *
     * @param id the ID of the promotion
     * @return an Optional containing the promotion if found, or empty if not found or not within the valid date range
     */
    @Query("SELECT p FROM Promotion p WHERE p.id = :id " +
            "AND (p.state = 'APPROVED' OR p.state = 'LIVE') " +
            "AND NOW() >= p.startDatetime " +
            "AND NOW() <= p.endDatetime")
    Optional<Promotion> findActiveOrApprovedPromotionWithinDateRange(@Param("id") Long id);
}