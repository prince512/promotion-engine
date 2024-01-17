package com.wasoko.promotion.service.strategies.benefits;


/**
 * The interface Benefit strategy.
 */
public interface BenefitStrategy {

    /**
     * Apply benefit double.
     *
     * @param originalPrice the original price
     * @param benefitValue  the benefit value
     * @return the double
     */
    double applyBenefit(double originalPrice, double benefitValue);

}
