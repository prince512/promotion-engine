package com.wasoko.promotion.service.repository;

import com.wasoko.promotion.service.entities.PromotionOfferMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * The interface Promotion offer map repository.
 */
public interface PromotionOfferMapRepository extends JpaRepository<PromotionOfferMap, Long> {

    /**
     * Retrieves a list of PromotionOfferMap entities associated with the given offer IDs.
     * Only promotions that are in the 'LIVE' or 'APPROVED' state and fall within their valid date range are included.
     *
     * @param ids A set of offer IDs used to filter promotions.
     * @return A list of PromotionOfferMap entities that match the specified criteria.
     */
    @Query(value = "SELECT promotionOfferMap FROM PromotionOfferMap promotionOfferMap LEFT JOIN  promotionOfferMap.promotion p ON" +
            "    promotionOfferMap.promotion.id=p.id where promotionOfferMap.offer.id IN :ids " +
            "    AND (p.state = 'LIVE' OR p.state = 'APPROVED')" +
            "    AND NOW() >= p.startDatetime" +
            "    AND NOW() <= p.endDatetime")
    List<PromotionOfferMap> findByOffer_IdIn(@Param("ids") Set<Long> ids);



    /**
     * Retrieves a list of PromotionOfferMap objects associated with the given promotion ID.
     *
     * @param id The ID of the promotion to search for.
     * @return A list of PromotionOfferMap objects matching the provided promotion ID.
     */
    List<PromotionOfferMap> findByPromotion_Id(@Param("id") Long id);

}