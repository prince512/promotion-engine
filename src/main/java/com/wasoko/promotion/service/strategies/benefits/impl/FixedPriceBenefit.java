package com.wasoko.promotion.service.strategies.benefits.impl;

import com.wasoko.promotion.service.strategies.benefits.BenefitStrategy;
import org.springframework.stereotype.Service;


/**
 * The type Fixed price benefit.
 */
@Service
public class FixedPriceBenefit implements BenefitStrategy {

    @Override
    public double applyBenefit(double originalPrice, double benefitValue) {
        return benefitValue;
    }
}
