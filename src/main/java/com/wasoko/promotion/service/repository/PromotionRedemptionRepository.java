package com.wasoko.promotion.service.repository;


import com.wasoko.promotion.service.entities.PromotionRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Promotion redemption repository.
 */
@Repository
public interface PromotionRedemptionRepository extends JpaRepository<PromotionRedemption, Long> {

}
