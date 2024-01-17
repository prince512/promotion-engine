package com.wasoko.promotion.service.service;

import com.wasoko.promotion.service.aop.RootResponseDTO;
public interface PromotionDetailService {

    RootResponseDTO getPromotion(Long promotionId);
}