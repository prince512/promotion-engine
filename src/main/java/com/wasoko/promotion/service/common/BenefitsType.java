package com.wasoko.promotion.service.common;


/**
 * The enum Benefits type.
 */
public enum BenefitsType {
    /**
     * Represents a percentage-based benefit.
     * Customers receive a percentage reduction in price when this benefit is applied.
     */
    PERCENTAGE,

    /**
     * Represents a fixed price benefit.
     * Customers pay a fixed, reduced price when this benefit is applied.
     */
    FIXED_PRICE,

    /**
     * Represents a fixed discount benefit.
     * Customers receive a fixed monetary discount when this benefit is applied.
     */
    FIXED_DISCOUNT,

    /**
     * Represents a shipping percentage-off benefit.
     * Customers receive a percentage reduction in shipping costs when this benefit is applied.
     */
    SHIPPING_PERCENTAGE_OFF,

    /**
     * Represents a shipping absolute-off benefit.
     * Customers receive a fixed monetary reduction in shipping costs when this benefit is applied.
     */
    SHIPPING_ABSOLUTE_OFF,

    /**
     * Represents a shipping fixed-price-off benefit.
     * Customers pay a fixed, reduced shipping price when this benefit is applied.
     */
    SHIPPING_FIXED_PRICE_OFF

}