package com.wasoko.promotion.service.strategies.conditions;


/**
 * The interface Condition strategy.
 */
public interface ConditionStrategy {

    /**
     * Check condition boolean.
     *
     * @param quantity       the quantity
     * @param value          the value
     * @param conditionValue the condition value
     * @return the boolean
     */
    boolean checkCondition(int quantity, double value, int conditionValue);

}
