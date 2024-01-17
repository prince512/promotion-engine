package com.wasoko.promotion.service.strategies.conditions.impl;

import com.wasoko.promotion.service.strategies.conditions.ConditionStrategy;
import org.springframework.stereotype.Service;

/**
 * The type Value condition.
 */
@Service
public class ValueCondition implements ConditionStrategy {
    @Override
    public boolean checkCondition(int quantity, double value, int conditionValue) {
        return conditionValue > (value * quantity);
    }
}
