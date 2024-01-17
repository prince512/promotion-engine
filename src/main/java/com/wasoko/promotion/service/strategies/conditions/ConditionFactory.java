package com.wasoko.promotion.service.strategies.conditions;

import com.wasoko.promotion.service.common.ConditionType;
import com.wasoko.promotion.service.exception.BusinessException;
import com.wasoko.promotion.service.strategies.conditions.impl.CountCondition;
import com.wasoko.promotion.service.strategies.conditions.impl.UnconditionalCondition;
import com.wasoko.promotion.service.strategies.conditions.impl.ValueCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The type Condition factory.
 */
@Component
@Slf4j
public class ConditionFactory {

    @Autowired
    private UnconditionalCondition unconditionalCondition;
    @Autowired
    private ValueCondition valueCondition;
    @Autowired
    private CountCondition countCondition;

    /**
     * Check condition boolean.
     *
     * @param quantity       the quantity
     * @param value          the value
     * @param conditionType  the condition type
     * @param conditionValue the condition value
     * @return the boolean
     */
    public boolean checkCondition(int quantity, double value, ConditionType conditionType, int conditionValue) {
        ConditionStrategy conditionStrategy = getConditionStrategy(conditionType);
        if (conditionStrategy == null) {
            log.error("Unsupported condition strategy type: {}", conditionType);
            throw new BusinessException(String.format("Unsupported condition type: %s ", conditionType));
        }
        return conditionStrategy.checkCondition(quantity, value, conditionValue);
    }

    private ConditionStrategy getConditionStrategy(ConditionType conditionType) {
        return switch (conditionType) {
            case COUNT -> countCondition;
            case UNCONDITIONAL -> unconditionalCondition;
            case VALUE -> valueCondition;
        };
    }
}
