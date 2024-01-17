package com.wasoko.promotion.service.service.impl;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.common.BenefitsType;
import com.wasoko.promotion.service.dto.request.Offer;
import com.wasoko.promotion.service.dto.request.PromotionRequest;
import com.wasoko.promotion.service.dto.response.OfferPromotionDetailsResponse;
import com.wasoko.promotion.service.dto.response.PromotionResponseDetailsDto;
import com.wasoko.promotion.service.dto.response.PromotionResponseDetailsDto.PromotionResponseDetailsDtoBuilder;
import com.wasoko.promotion.service.entities.Promotion;
import com.wasoko.promotion.service.entities.PromotionBenefitFixedPrice;
import com.wasoko.promotion.service.entities.PromotionOfferMap;
import com.wasoko.promotion.service.exception.BusinessException;
import com.wasoko.promotion.service.repository.PromotionBenefitFixedPriceRepository;
import com.wasoko.promotion.service.repository.PromotionOfferMapRepository;
import com.wasoko.promotion.service.service.PromotionService;
import com.wasoko.promotion.service.strategies.benefits.BenefitFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Promotion service.
 */
@Slf4j
@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionOfferMapRepository promotionOfferMapRepository;
    private final PromotionBenefitFixedPriceRepository promotionBenefitFixedPriceRepository;
    private final BenefitFactory benefitFactory;

    /**
     * Instantiates a new Promotion service.
     *
     * @param promotionOfferMapRepository          the promotion offer map repository
     * @param promotionBenefitFixedPriceRepository the promotion benefit fixed price repository
     * @param benefitFactory                       the benefit factory
     */
    @Autowired
    public PromotionServiceImpl(PromotionOfferMapRepository promotionOfferMapRepository, PromotionBenefitFixedPriceRepository promotionBenefitFixedPriceRepository, BenefitFactory benefitFactory) {
        this.promotionOfferMapRepository = promotionOfferMapRepository;
        this.promotionBenefitFixedPriceRepository = promotionBenefitFixedPriceRepository;
        this.benefitFactory = benefitFactory;
    }

    /**
     * Step 1: Using offerIds from offerRequest, get List of promotionsOfferMap (having map of
     * Promotions and Offers under this promotion) Step 2: Create Map of Offers and List of Promotions
     * with each Offer within. Step 3: For each Offer under this Map, Apply Promotion for each
     * Promotion applicable to this Offer. Create a new structure like Offer Details [+Promotional
     * details[]]
     */
    @Override
    public RootResponseDTO getPromotions(PromotionRequest promotionRequest) {

        Set<Long> offerIds = promotionRequest.getOffers().stream().map(Offer::getOfferId)
                .collect(Collectors.toSet());

        log.info("Received request to get promotions for offerRequest: {}", promotionRequest);

        List<OfferPromotionDetailsResponse> offerPromotionDetailsResponseList = new ArrayList<>();
        List<PromotionOfferMap> promotionOfferList = promotionOfferMapRepository.findByOffer_IdIn(
                offerIds);
        if (promotionOfferList.isEmpty()) {
            log.info("No promotions found for the given offers: {}", offerIds);
            return new RootResponseDTO(promotionRequest.getOffers().stream()
                    .map(offer -> OfferPromotionDetailsResponse.builder()
                            .offerId(offer.getOfferId())
                            .originalPrice(offer.getOriginalPrice())
                            .promotions(PromotionResponseDetailsDto.builder()
                                    .discountedPrice(offer.getOriginalPrice()).discountPercentage("0%")
                                    .build())
                            .build()).toList());

        }

        List<Promotion> promotions = getPromotionsFromPromotionOfferMapList(promotionOfferList);

        List<PromotionBenefitFixedPrice> promotionBenefitFixedPrices = getFixedPricesForPromotions(
                promotions);

        /**
         *  Map to store the calculated offer prices for each promotion and offer combination.
         *  The outer Map's key represents the promotion ID, and the value is an inner Map.
         *  The inner Map's key represents the offer ID, and the value is the calculated offer price.
         */
        Map<Long, Map<Long, Float>> promotionOfferPriceMap = getPromotionOfferFixedPrices(
                promotionBenefitFixedPrices);

        Map<Long, List<PromotionOfferMap>> promotionsOfferIdMap = groupPromotionOfferMapByOfferId(
                promotionOfferList);

        for (Offer offer : promotionRequest.getOffers()) {
            offerPromotionDetailsResponseList.add(
                    getPromotionOfferDetails(offer, promotionsOfferIdMap, promotionOfferPriceMap));
        }

        log.info("Returning promotions details for given offer request:  {}",
                offerPromotionDetailsResponseList);

        return new RootResponseDTO(offerPromotionDetailsResponseList);
    }

    private OfferPromotionDetailsResponse getPromotionOfferDetails(Offer offer,
                                                                   Map<Long, List<PromotionOfferMap>> offerIdPromotionsMap,
                                                                   Map<Long, Map<Long, Float>> promotionOfferPriceMap) {
        Long offerId = offer.getOfferId();
        if (!offerIdPromotionsMap.containsKey(offerId) || offerIdPromotionsMap.get(offerId).isEmpty()) {
            return OfferPromotionDetailsResponse.builder().offerId(offerId).originalPrice(offer.getOriginalPrice())
                    .promotions(PromotionResponseDetailsDto.builder().discountedPrice(offer.getOriginalPrice()).build()).build();
        }

        List<PromotionOfferMap> promotionOfferList = offerIdPromotionsMap.get(offerId);
        OfferPromotionDetailsResponse offerPromotionDetailsResponse = new OfferPromotionDetailsResponse();
        PriorityQueue<PromotionResponseDetailsDto> promotionResponseDetailDtos = new PriorityQueue<>(
                Comparator.comparingDouble(PromotionResponseDetailsDto::getDiscountedPrice));

        for (PromotionOfferMap promotionOfferMap : promotionOfferList) {
            Promotion promotion = promotionOfferMap.getPromotion();
            if (promotion.getPromotionBenefits() != null) {
                promotionResponseDetailDtos.add(getPromotionResponseDetails(promotionOfferMap, promotionOfferPriceMap, offer));
            }
        }
        offerPromotionDetailsResponse.setOfferId(offerId);
        offerPromotionDetailsResponse.setOriginalPrice(offer.getOriginalPrice());

        offerPromotionDetailsResponse.setPromotions(promotionResponseDetailDtos.peek() == null ? PromotionResponseDetailsDto.builder().build() : promotionResponseDetailDtos.peek());
        return offerPromotionDetailsResponse;
    }

    private PromotionResponseDetailsDto getPromotionResponseDetails(
            PromotionOfferMap promotionOfferMap, Map<Long, Map<Long, Float>> promotionOfferPriceMap,
            Offer offer) {

        double originalPrice = offer.getOriginalPrice() != 0 ? offer.getOriginalPrice() : 0;

        Promotion promotion = promotionOfferMap.getPromotion();
        BenefitsType benefitType = BenefitsType.valueOf(promotion.getPromotionBenefits().getType().toUpperCase());
        double benefitValue;
        PromotionResponseDetailsDtoBuilder promotionResponseDetailsBuilder = mapPromotionDetails(
                promotion);

        if (benefitType.equals(BenefitsType.FIXED_PRICE) && (
                promotionOfferPriceMap.get(promotion.getId()) == null
                        || !promotionOfferPriceMap.get(promotion.getId()).containsKey(offer.getOfferId()))) {
            log.error("Promotional price for offer {} under promotion {} is not found",
                    offer.getOfferId(), promotion.getId());
            return PromotionResponseDetailsDto.builder().discountedPrice(originalPrice)
                    .discountPercentage("0 %")
                    .build();
        } else if (benefitType.equals(BenefitsType.FIXED_PRICE)) {
            benefitValue = promotionOfferPriceMap.get(promotion.getId()).get(offer.getOfferId());
        } else {
            benefitValue = promotion.getPromotionBenefits().getValue();
        }
        double discountedPrice = originalPrice;
        try {
            discountedPrice = benefitFactory.applyBenefit(originalPrice, benefitValue, benefitType);
        } catch (BusinessException e) {
            log.error("Could not apply benefit {}", benefitType);
        }

        return promotionResponseDetailsBuilder.discountedPrice(discountedPrice)
                .discount(benefitValue)
                .discountPercentage(String.format("%.2f%%", (benefitValue / originalPrice) * 100.0))
                .build();

    }

    private PromotionResponseDetailsDtoBuilder mapPromotionDetails(Promotion promotion) {
        return PromotionResponseDetailsDto.builder()
                .promotionName(promotion.getName())
                .promotionType(promotion.getType())
                .promotionId(promotion.getId())
                .endTime(promotion.getEndDatetime());
    }

    private List<Promotion> getPromotionsFromPromotionOfferMapList(
            List<PromotionOfferMap> promotionOfferMapList) {
        return promotionOfferMapList.stream()
                .map(PromotionOfferMap::getPromotion)
                .toList();
    }

    private List<PromotionBenefitFixedPrice> getFixedPricesForPromotions(List<Promotion> promotions) {
        return promotionBenefitFixedPriceRepository.findByPromotionIn(promotions);
    }

    private Map<Long, List<PromotionOfferMap>> groupPromotionOfferMapByOfferId(
            List<PromotionOfferMap> promotionDumps) {
        return promotionDumps.stream()
                .collect(
                        Collectors.groupingBy(promotionOfferMap -> promotionOfferMap.getOffer().getId()));
    }

    private Map<Long, Map<Long, Float>> getPromotionOfferFixedPrices(
            List<PromotionBenefitFixedPrice> promotionBenefitFixedPrices) {
        return promotionBenefitFixedPrices.stream()
                .collect(Collectors.toMap(
                        promotionBenefitFixedPrice -> promotionBenefitFixedPrice.getPromotion().getId(),
                        this::createOfferPriceMap,
                        this::mergOfferPriceMap
                ));
    }

    private Map<Long, Float> createOfferPriceMap(
            PromotionBenefitFixedPrice promotionBenefitFixedPrice) {
        Map<Long, Float> innerMap = new HashMap<>();
        innerMap.put(promotionBenefitFixedPrice.getOffer().getId(),
                promotionBenefitFixedPrice.getFixedPrice());
        return innerMap;
    }

    private Map<Long, Float> mergOfferPriceMap(Map<Long, Float> existingMap,
                                               Map<Long, Float> newMap) {
        existingMap.putAll(newMap);
        return existingMap;

    }
}
