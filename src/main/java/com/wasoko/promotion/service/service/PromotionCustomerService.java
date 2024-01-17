package com.wasoko.promotion.service.service;

import com.wasoko.promotion.service.aop.RootResponseDTO;

/**
 * The interface Promotion customer service.
 */
public interface PromotionCustomerService {

    /**
     * Gets promotions for customer and serviceability area.
     *
     * @param serviceabilityAreaId the serviceability area id
     * @param customerId           the customer id
     * @return the promotions for customer and serviceability area
     */
    RootResponseDTO getPromotionsForCustomerAndServiceabilityArea(String serviceabilityAreaId,
                                                                  String customerId);

}
