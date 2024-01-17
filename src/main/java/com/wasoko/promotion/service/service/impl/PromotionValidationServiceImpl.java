package com.wasoko.promotion.service.service.impl;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.common.BenefitsType;
import com.wasoko.promotion.service.common.ConditionType;
import com.wasoko.promotion.service.dto.request.OfferDetails;
import com.wasoko.promotion.service.dto.request.PromotionValidationRequestDto;
import com.wasoko.promotion.service.dto.response.OfferPromotionDto;
import com.wasoko.promotion.service.dto.response.OfferPromotionResponse;
import com.wasoko.promotion.service.entities.*;
import com.wasoko.promotion.service.exception.BusinessException;
import com.wasoko.promotion.service.repository.PromotionOfferMapRepository;
import com.wasoko.promotion.service.service.PromotionValidationService;
import com.wasoko.promotion.service.strategies.benefits.BenefitFactory;
import com.wasoko.promotion.service.strategies.conditions.ConditionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wasoko.promotion.service.common.util.PromotionConstraintsUtil.*;

/**
 * The type Promotion validation service.
 */
@Slf4j
@Service
public class PromotionValidationServiceImpl implements PromotionValidationService {
    private final PromotionOfferMapRepository promotionOfferMapRepository;
    private final ConditionFactory conditionFactory;
    private final BenefitFactory benefitFactory;

    /**
     * Instantiates a new Promotion validation service.
     *
     * @param promotionOfferMapRepository the promotion offer map repository
     * @param conditionFactory            the condition factory
     * @param benefitFactory              the benefit factory
     */
    @Autowired
    public PromotionValidationServiceImpl(PromotionOfferMapRepository promotionOfferMapRepository, ConditionFactory conditionFactory, BenefitFactory benefitFactory) {
        this.promotionOfferMapRepository = promotionOfferMapRepository;
        this.conditionFactory = conditionFactory;
        this.benefitFactory = benefitFactory;
    }

    /**
     * 1. Get Offer IDs: - Extract the offer IDs from the `promotionValidationRequest` using a stream
     * operation and collect them into a `Set`.
     * <p>
     * 2. Check for Duplicate Offers: 2.1 Compare the size of the extracted offer IDs set with the
     * number of offer details in the `promotionValidationRequest` to identify any duplicate offers.
     * 2.2 If duplicates are found, log an error and throw a `BusinessException` indicating the
     * presence of duplicate offers.
     * <p>
     * 3. Fetch Promotions for Offer IDs: 3.1 Retrieve a list of `PromotionOfferMap` entities from the
     * database using the `offerIds` set. 3.2 Create an empty `OfferPromotionResponse` object to store
     * the results.
     * <p>
     * 4. Handle No Promotions Case: 4.1 If no promotions are found for the given offers, iterate
     * through each `OfferDetails` and create an `OfferPromotionDto` with no promotions, using the
     * original price as the discounted price. 4.2 Add these DTOs to the `offers` list in the
     * OfferPromotionResponse and return.
     * <p>
     * 5. Create Offer-Promotion Map: 5.1 Create a map (offerIdToPromotionsMap) where offer IDs are
     * mapped to lists of associated promotions from the retrieved PromotionOfferMap entities.
     * <p>
     * 6. Process Each Offer: 6.1 Iterate through each OfferDetails in the promotionValidationRequest.
     * 6.2 Check if the offerIdToPromotionsMap contains promotions for the current offer ID.
     * <p>
     * 7. Find Best Promotion for Offer: 7.1 If promotions are available for the current offer, call
     * the findBestPromotionForOffer method to determine the best promotion for the offer based on
     * various constraints and benefits. 7.2 If no promotions are available, still call
     * findBestPromotionForOffer to create an OfferPromotionDto with no promotions.
     * <p>
     * 8. Build Response: 8.1 Add the generated OfferPromotionDto to the offers list in the
     * OfferPromotionResponse.
     * <p>
     * 9. Return Result: 9.1 Return the OfferPromotionResponse containing the list of
     * OfferPromotionDto objects, each representing the best promotion and without promotion
     * information for an offer.
     */
    @Override
    public RootResponseDTO validatePromotions(
            PromotionValidationRequestDto promotionValidationRequestDto) {
        log.info(
                "Received promotion validation request to get best promotion for promotionValidationRequest size: {} promotionValidationRequest: {}",
                promotionValidationRequestDto.getOfferDetails().size(), promotionValidationRequestDto);
        Set<Long> offerIds = promotionValidationRequestDto.getOfferDetails().stream()
                .map(OfferDetails::getOfferId).collect(Collectors.toSet());


        List<PromotionOfferMap> promotionsOfferMap = promotionOfferMapRepository.findByOffer_IdIn(offerIds);
        OfferPromotionResponse offerPromotionResponse = new OfferPromotionResponse();
        offerPromotionResponse.setOfferPromotionDtoList(new ArrayList<>());

        if (promotionsOfferMap.isEmpty()) {
            log.info("No promotions found for the given offers: {}", offerIds);
            for (OfferDetails offerDetails : promotionValidationRequestDto.getOfferDetails()) {
                offerPromotionResponse.getOfferPromotionDtoList().add(OfferPromotionDto.builder()
                        .offerId(offerDetails.getOfferId())
                        .originalPrice(offerDetails.getPrice()).discountedPrice(offerDetails.getPrice())
                        .calculatedDiscount(0).calculatedDiscountPercentage("0 %")
                        .build());
            }

            return new RootResponseDTO(offerPromotionResponse);
        }

        Map<Long, List<Promotion>> offerIdToPromotionsMap = promotionsOfferMap.stream()
                .collect(Collectors.groupingBy(promotionOfferMap -> promotionOfferMap.getOffer().getId(),
                        Collectors.mapping(PromotionOfferMap::getPromotion, Collectors.toList())
                ));

        for (OfferDetails offerDetails : promotionValidationRequestDto.getOfferDetails()) {
            if (offerIdToPromotionsMap.containsKey(offerDetails.getOfferId())) {
                offerPromotionResponse.getOfferPromotionDtoList().add(findBestPromotionForOffer(offerDetails,
                        offerIdToPromotionsMap.get(offerDetails.getOfferId()), promotionValidationRequestDto.getCustomerId()));
            } else {
                offerPromotionResponse.getOfferPromotionDtoList().add(OfferPromotionDto.builder()
                        .offerId(offerDetails.getOfferId())
                        .originalPrice(offerDetails.getPrice()).discountedPrice(offerDetails.getPrice())
                         .calculatedDiscount(0)
                        .calculatedDiscountPercentage("0 %")
                        .build());
            }
        }

        return new RootResponseDTO(offerPromotionResponse);
    }

    private OfferPromotionDto findBestPromotionForOffer(OfferDetails offerDetails,
                                                        List<Promotion> promotions,
                                                        String customerID) {
        Promotion bestPromotion = null;
        double bestDiscount = 0F;
        for (Promotion promotion : promotions) {
            List<PromotionRedemption> filteredRedemptions = filterPromotionsForCustomerAndOffer(offerDetails.getOfferId(),
                    promotion, customerID);
            PromotionCondition promotionCondition = promotion.getPromotionCondition();
            ConditionType conditionType = ConditionType.valueOf(promotionCondition.getType());
            try {
                if (!conditionFactory.checkCondition(offerDetails.getQuantity(), offerDetails.getPrice(),
                        conditionType, promotionCondition.getValue())) {
                    log.info("Promotion not applied. Condition not met for promotion: {} and offer with details: {}", promotion.getId(), offerDetails);
                    continue;
                }
            } catch (BusinessException e) {
                log.error("Unable to check condition {}", conditionType);
                continue;
            }

            boolean isPromotionValid = isPromotionValid(offerDetails.getOfferId(), promotion, customerID);
            Float availedDiscount = getAvailedDiscount(filteredRedemptions);
            Float globalAvailedDiscount = getGlobalAvailedDiscount(promotion.getPromotionRedemptionList());
            if (isPromotionValid) {
                double discountedPrice = 0;
                if (promotion.getPromotionBenefits() != null)
                    discountedPrice = applyBenefits(offerDetails, promotion);
                double discount = offerDetails.getPrice() - discountedPrice;
                double globalRemainingDiscount =
                        promotion.getMaxGlobalDiscount() != null ? promotion.getMaxGlobalDiscount().floatValue() - globalAvailedDiscount : 0;
                double userRemainingDiscount = promotion.getMaxDiscount() != null ? promotion.getMaxDiscount() - availedDiscount : 0;
                double finalBenefit = Math.min(globalRemainingDiscount,
                        Math.min(userRemainingDiscount, discount));

                if (finalBenefit > bestDiscount) {
                    bestDiscount = finalBenefit;
                    bestPromotion = promotion;
                }
            }
        }

        return createOfferPromotionDto(offerDetails, bestPromotion, bestDiscount);
    }

    private double applyBenefits(OfferDetails offerDetails, Promotion promotion) {

        PromotionBenefit promotionBenefit = promotion.getPromotionBenefits();
        String benefitType = promotionBenefit.getType();
        BenefitsType benefitsType = BenefitsType.valueOf(benefitType);
        double discountedPrice = offerDetails.getPrice();
        double benefitValue;
        if (benefitsType.equals(BenefitsType.FIXED_PRICE)) {

            Float fixedPriceForOfferId = getFixedPriceForOfferId(
                    promotion.getPromotionBenefitFixedPrices(), offerDetails.getOfferId());

            if (fixedPriceForOfferId == null) {
                log.error(
                        "Fixed price is not present for given promotion. Benefit Type: {}, promotion Id : {}",
                        benefitType, promotion.getId());

                benefitValue = offerDetails.getPrice();
            } else {
                benefitValue = fixedPriceForOfferId;
            }
        } else {
            benefitValue = promotionBenefit.getValue();
        }
        try {
            discountedPrice = benefitFactory.applyBenefit(offerDetails.getPrice(), benefitValue,
                    benefitsType);
        } catch (BusinessException e) {
            log.error("Could not apply benefit {}", benefitType);
        }
        return discountedPrice;
    }

    private OfferPromotionDto createOfferPromotionDto(OfferDetails offerDetails,
                                                      Promotion bestPromotion,
                                                      double bestDiscount) {
        return OfferPromotionDto.builder()
                .offerId(offerDetails.getOfferId())
                .promoId(bestPromotion != null ? bestPromotion.getId() : null)
                .name((bestPromotion != null) ? bestPromotion.getName() : null)
                .originalPrice(offerDetails.getPrice())
                .type((bestPromotion != null) ? bestPromotion.getType() : null)
                .discountedPrice(offerDetails.getPrice() - bestDiscount)
                .calculatedDiscount(bestDiscount)
                .endDate((bestPromotion != null) ? String.valueOf(bestPromotion.getEndDatetime()) : null)
                .calculatedDiscountPercentage(String.format("%.2f%%", (bestDiscount / offerDetails.getPrice()) * 100.0)
                ).build();
    }

    /**
     * Gets fixed price for offer id.
     *
     * @param promotionBenefitFixedPrices the promotion benefit fixed prices
     * @param offerId                     the offer id
     * @return the fixed price for offer id
     */
    public Float getFixedPriceForOfferId(List<PromotionBenefitFixedPrice> promotionBenefitFixedPrices,
                                         Long offerId) {
        PromotionBenefitFixedPrice matchingBenefit = promotionBenefitFixedPrices.stream()
                .filter(offer -> offer.getOffer().getId().equals(offerId))
                .findFirst()
                .orElse(null);

        return (matchingBenefit != null) ? matchingBenefit.getFixedPrice() : null;
    }
}
