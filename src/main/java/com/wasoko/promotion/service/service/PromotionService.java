package com.wasoko.promotion.service.service;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.dto.request.PromotionRequest;

import java.io.IOException;


/**
 * The interface Promotion service.
 */
public interface PromotionService {

    /**
     * Gets promotions.
     *
     * @param promotionRequest the offer request
     * @return the promotions
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    RootResponseDTO getPromotions(PromotionRequest promotionRequest);

}
