package com.wasoko.promotion.service.common;

/**
 * The enum Condition type.
 * Describes the types of conditions that can be associated with promotions.
 */
public enum ConditionType {
    /**
     * Unconditional condition type.
     * This type of condition does not have any specific requirements for the promotion to be applied.
     * It's applied without any constraints.
     */
    UNCONDITIONAL,

    /**
     * Count condition type.
     * This type of condition is based on the count of qty where a minimum quantity threshold is required
     */
    COUNT,

    /**
     * Value condition type.
     * This type of condition is based on the total value of the qty.
     * For example, "Get 10% off on orders above $100" would be a value-based condition.
     */
    VALUE
}
