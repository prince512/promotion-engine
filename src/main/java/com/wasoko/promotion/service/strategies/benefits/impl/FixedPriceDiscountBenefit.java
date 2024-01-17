package com.wasoko.promotion.service.strategies.benefits.impl;

import com.wasoko.promotion.service.strategies.benefits.BenefitStrategy;
import org.springframework.stereotype.Service;


/**
 * The type Fixed price discount benefit.
 */
@Service
public class FixedPriceDiscountBenefit implements BenefitStrategy {

    @Override
    public double applyBenefit(double originalPrice, double benefitValue) {
        return originalPrice - benefitValue;
    }
}
