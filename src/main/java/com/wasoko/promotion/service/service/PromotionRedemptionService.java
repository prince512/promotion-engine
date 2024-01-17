package com.wasoko.promotion.service.service;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.dto.request.PromotionRedemptionRequestDto;

/**
 * The interface Promotion redemption service.
 */
public interface PromotionRedemptionService {

    /**
     * Apply redemption promotion redemption response dto.
     *
     * @param request the request
     * @return the promotion redemption response dto
     */
    RootResponseDTO applyRedemption(PromotionRedemptionRequestDto request);
}
