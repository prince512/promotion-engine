package com.wasoko.promotion.service.exception;

public class PromotionNotFoundException extends RuntimeException {

    private final Long promotionId;

    public PromotionNotFoundException(Long promotionId) {
        super("Promotion not found with ID: " + promotionId);
        this.promotionId = promotionId;
    }

    public Long getPromotionId() {
        return promotionId;
    }
}
