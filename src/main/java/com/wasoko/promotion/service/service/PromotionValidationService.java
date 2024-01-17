package com.wasoko.promotion.service.service;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.dto.request.PromotionValidationRequestDto;

/**
 * The interface Promotion validation.
 */
public interface PromotionValidationService {

    /**
     * Validate promotions root response dto.
     *
     * @param promotionValidationRequestDto the promotion validation request
     * @return the root response dto
     */
    RootResponseDTO validatePromotions(PromotionValidationRequestDto promotionValidationRequestDto);

}
