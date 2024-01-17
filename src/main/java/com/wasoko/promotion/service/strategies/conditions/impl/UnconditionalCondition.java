package com.wasoko.promotion.service.strategies.conditions.impl;

import com.wasoko.promotion.service.strategies.conditions.ConditionStrategy;
import org.springframework.stereotype.Service;


/**
 * The type Unconditional condition.
 */
@Service
public class UnconditionalCondition implements ConditionStrategy {

    @Override
    public boolean checkCondition(int quantity, double value, int conditionValue) {
        return true;
    }
}
