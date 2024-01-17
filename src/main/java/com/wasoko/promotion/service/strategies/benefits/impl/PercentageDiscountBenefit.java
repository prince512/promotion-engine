package com.wasoko.promotion.service.strategies.benefits.impl;

import com.wasoko.promotion.service.strategies.benefits.BenefitStrategy;
import org.springframework.stereotype.Service;

/**
 * The type Percentage discount benefit.
 */
@Service
public class PercentageDiscountBenefit implements BenefitStrategy {


    @Override
    public double applyBenefit(double originalPrice, double benefitValue) {
        return originalPrice - (originalPrice * benefitValue / 100);

    }
}
