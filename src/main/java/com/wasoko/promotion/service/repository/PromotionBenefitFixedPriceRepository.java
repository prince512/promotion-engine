package com.wasoko.promotion.service.repository;

import com.wasoko.promotion.service.entities.Promotion;
import com.wasoko.promotion.service.entities.PromotionBenefitFixedPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The interface Promotion benefit fixed price repository.
 */
@Repository
public
interface PromotionBenefitFixedPriceRepository extends
        JpaRepository<PromotionBenefitFixedPrice, Long> {
    /**
     * Find by promotion in list.
     *
     * @param promotions the promotions
     * @return the list
     */
    List<PromotionBenefitFixedPrice> findByPromotionIn(@Param("promotions") List<Promotion> promotions);

}
