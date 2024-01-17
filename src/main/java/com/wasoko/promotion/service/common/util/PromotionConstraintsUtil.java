package com.wasoko.promotion.service.common.util;

import com.wasoko.promotion.service.entities.Promotion;
import com.wasoko.promotion.service.entities.PromotionRedemption;

import java.util.List;
import java.util.UUID;

import static com.wasoko.promotion.service.common.Constants.MAX_VALUE_LIMIT;

/**
 * Utility class for handling promotion constraints and calculations.
 */
public class PromotionConstraintsUtil {

    /**
     * Filters redemptions for a specific promotion based on offer details and customer ID.
     *
     * @param offerId    offer Id.
     * @param promotion  The promotion.
     * @param customerID The customer ID.
     * @return A list of filtered PromotionRedemption.
     */
    public static List<PromotionRedemption> filterPromotionsForCustomerAndOffer(
            Long offerId, Promotion promotion, String customerID) {
        return promotion.getPromotionRedemptionList().stream()
                .filter(redemption -> redemption.getCustomerId().equals(UUID.fromString(customerID))
                        && redemption.getOfferId().equals(offerId)
                        && redemption.getPromotion().getId().equals(promotion.getId())).toList();
    }

    /**
     * Filters redemptions for a specific promotion based on customer ID.
     *
     * @param promotion  The promotion.
     * @param customerID The customer ID.
     * @return A list of filtered PromotionRedemption.
     */
    public static List<PromotionRedemption> filterPromotionsForCustomer(
            Promotion promotion,
            String customerID) {
        return promotion.getPromotionRedemptionList().stream()
                .filter(redemption -> redemption.getCustomerId().equals(UUID.fromString(customerID))
                        && redemption.getPromotion().getId().equals(promotion.getId())).toList();
    }

    /**
     * Calculates the total availed discount from a list of filtered redemptions.
     *
     * @param filteredRedemptions The filtered redemptions.
     * @return The total availed discount.
     */
    public static Float getAvailedDiscount(List<PromotionRedemption> filteredRedemptions) {
        return filteredRedemptions.stream()
                .map(PromotionRedemption::getDiscountAvailed)
                .reduce(0F, Float::sum);
    }

    /**
     * Calculates the total global availed discount for a promotion.
     *
     * @param promotionRedemptionList The PromotionRedemption list.
     * @return The total global availed discount.
     */
    public static Float getGlobalAvailedDiscount(List<PromotionRedemption> promotionRedemptionList) {
        return promotionRedemptionList.stream()
                .map(PromotionRedemption::getDiscountAvailed)
                .reduce(0F, Float::sum);
    }

    /**
     * Calculates the total global redemption quantity for a promotion.
     *
     * @param promotionRedemptionList for promotionRedemption.
     * @return The total global redemption quantity.
     */
    public static int getGlobalRedemptionQty(List<PromotionRedemption> promotionRedemptionList) {
        return promotionRedemptionList.stream()
                .mapToInt(PromotionRedemption::getQuantity)
                .sum();
    }

    /**
     * Calculates the total customer redemption quantity from a list of filtered redemptions.
     *
     * @param filteredRedemptions The filtered redemptions.
     * @return The total customer redemption quantity.
     */
    public static int getCustomerRedemptionQty(List<PromotionRedemption> filteredRedemptions) {
        return filteredRedemptions.stream()
                .mapToInt(PromotionRedemption::getQuantity)
                .sum();
    }

    /**
     * Checks if a promotion is valid based on various constraints and criteria.
     *
     * @param offerId    The offer Id.
     * @param promotion  The promotion.
     * @param customerId The customer ID.
     * @return `true` if the promotion is valid; otherwise, `false`.
     */
    public static boolean isPromotionValid(Long offerId, Promotion promotion,
                                           String customerId) {
        List<PromotionRedemption> filteredRedemptions;
        if (offerId == null) {
            filteredRedemptions = filterPromotionsForCustomer(promotion, customerId);
        } else {
            filteredRedemptions = filterPromotionsForCustomerAndOffer(offerId, promotion, customerId);
        }
        Float availedDiscount = getAvailedDiscount(filteredRedemptions);
        Float globalAvailedDiscount = getGlobalAvailedDiscount(promotion.getPromotionRedemptionList());
        int customerAvailedRedemptionCount = filteredRedemptions.size();
        int globalRedemptionCount = promotion.getPromotionRedemptionList().size();
        int globalRedemptionQty = getGlobalRedemptionQty(promotion.getPromotionRedemptionList());
        int customerRedemptionQty = getCustomerRedemptionQty(filteredRedemptions);

        int remainingQtyAvailableForRedemption = Math.min(
                (promotion.getPromotionLimit() != null) ? (
                        promotion.getPromotionLimit().getLimitPerCustomer()
                                - customerRedemptionQty) : MAX_VALUE_LIMIT,
                (promotion.getPromotionLimit() != null) ? (promotion.getPromotionLimit().getMaxCount()
                        - globalRedemptionQty) : MAX_VALUE_LIMIT);

        boolean globalConstraintNotReached = isGlobalConstraintNotReached(promotion,
                globalRedemptionCount,
                globalAvailedDiscount);

        boolean customerConstraintNotReached = isCustomerConstraintNotReached(promotion,
                customerAvailedRedemptionCount, availedDiscount);

        return globalConstraintNotReached && customerConstraintNotReached && (remainingQtyAvailableForRedemption > 0);
    }

    private static boolean isCustomerConstraintNotReached(Promotion promotion,
                                                          int customerAvailedRedemptionCount, Float availedDiscount) {
        return (promotion.getMaxUserApplications() > customerAvailedRedemptionCount)
                && (promotion.getMaxDiscount() > availedDiscount);
    }

    private static boolean isGlobalConstraintNotReached(Promotion promotion,
                                                        int globalRedemptionCount, Float globalAvailedDiscount) {
        return (promotion.getMaxGlobalApplications() > globalRedemptionCount)
                && (promotion.getMaxGlobalDiscount().floatValue() > globalAvailedDiscount);
    }

}
