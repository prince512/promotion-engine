package com.wasoko.promotion.service.strategies.benefits;

import com.wasoko.promotion.service.common.BenefitsType;
import com.wasoko.promotion.service.exception.BusinessException;
import com.wasoko.promotion.service.strategies.benefits.impl.FixedPriceBenefit;
import com.wasoko.promotion.service.strategies.benefits.impl.FixedPriceDiscountBenefit;
import com.wasoko.promotion.service.strategies.benefits.impl.PercentageDiscountBenefit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The type Benefit factory.
 */
@Component
@Slf4j
public class BenefitFactory {

    /**
     * The Fixed price benefit.
     */
    @Autowired
    FixedPriceBenefit fixedPriceBenefit;
    @Autowired
    private FixedPriceDiscountBenefit fixedPriceDiscountBenefit;
    @Autowired
    private PercentageDiscountBenefit percentageDiscountBenefit;

    /**
     * Apply benefit double.
     *
     * @param originalPrice the original price
     * @param benefitValue  the benefit value
     * @param benefitType   the benefit type
     * @return the double
     */
    public double applyBenefit(double originalPrice, double benefitValue, BenefitsType benefitType) {
        BenefitStrategy benefitStrategy = getBenefitStrategy(benefitType);
        if (benefitStrategy == null) {
            log.error("Unsupported benefit strategy type: {}", benefitType);
            throw new BusinessException(String.format("Unsupported benefit type: %s ", benefitType));
        }
        return benefitStrategy.applyBenefit(originalPrice, benefitValue);
    }

    private BenefitStrategy getBenefitStrategy(BenefitsType benefitsType) {
        return switch (benefitsType) {
            case FIXED_DISCOUNT -> fixedPriceDiscountBenefit;
            case PERCENTAGE -> percentageDiscountBenefit;
            case FIXED_PRICE -> fixedPriceBenefit;
            default -> null;
        };
    }
}
