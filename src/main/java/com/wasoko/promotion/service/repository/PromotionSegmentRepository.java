package com.wasoko.promotion.service.repository;

import com.wasoko.promotion.service.entities.PromotionSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * The interface Promotion segment repository.
 */
@Repository
public interface PromotionSegmentRepository extends JpaRepository<PromotionSegment, Long> {


    /**
     * Case 1 : This will give the promo IDs with segments and SA matching customer segments.
     * Case 2 : This will give the promotion IDs with SA matching customer & segments being empty.
     * Retrieves a list of promotion IDs that match based on both serviceability areas (SAs)
     * and customer ID. This query can also handle both SAs and segments independently.
     * @param serviceabilityAreaId The unique identifier of the serviceability area (SA).
     * @param customerId           The unique identifier of the customer.
     * @return A list of promotion IDs that satisfy the specified conditions.
     */
    @Query(value = "SELECT p.id " +
            "FROM promotion_promotion p " +
            "         INNER JOIN promotion_promotion_serviceability_areas psa ON p.id = psa.promotion_id " +
            "         LEFT JOIN promotion_promotion_segments ps ON p.id = ps.promotion_id " +
            "         LEFT JOIN promotion_segmentcustomer s ON ps.segment_id = s.segment_id " +
            "WHERE (ps.segment_id IS NULL OR s.customer_id = :customer_id) " +
            "  AND psa.serviceabilityarea_id = :serviceability_area " +
            "  AND (p.state = 'APPROVED' OR p.state = 'LIVE') " +
            "  AND NOW() >= p.start_datetime " +
            "  AND NOW() <= p.end_datetime", nativeQuery = true)
    List<Long> findPromotionsByServiceabilityAreaIdAndCustomerID(
            @Param("serviceability_area") UUID serviceabilityAreaId,
            @Param("customer_id") UUID customerId);

}