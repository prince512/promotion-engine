package com.wasoko.promotion.service.service.impl;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.dto.response.*;
import com.wasoko.promotion.service.entities.*;
import com.wasoko.promotion.service.exception.PromotionNotFoundException;
import com.wasoko.promotion.service.repository.PromotionOfferMapRepository;
import com.wasoko.promotion.service.repository.PromotionRepository;
import com.wasoko.promotion.service.service.PromotionDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * The type Promotion detail service.
 */
@Service
@Slf4j
public class PromotionDetailServiceImpl implements PromotionDetailService {

    private final PromotionRepository promotionRepository;

    private final PromotionOfferMapRepository promotionOfferMapRepository;

    /**
     * Instantiates a new Promotion detail service.
     *
     * @param promotionRepository the promotion repository
     */
    @Autowired
    public PromotionDetailServiceImpl(PromotionRepository promotionRepository, PromotionOfferMapRepository promotionOfferMapRepository) {
        this.promotionRepository = promotionRepository;
        this.promotionOfferMapRepository = promotionOfferMapRepository;
    }

    @Override
    public RootResponseDTO getPromotion(Long promotionId) {
        Optional<Promotion> promotion = promotionRepository.findActiveOrApprovedPromotionWithinDateRange(promotionId);
        if (promotion.isEmpty()) {
            log.warn("No promotion found with ID : {}.", promotionId);
            throw new PromotionNotFoundException(promotionId);
        }

        return new RootResponseDTO(createPromotionDetailsResponse(promotion.get()));
    }

    private PromotionDetailsResponseDto createPromotionDetailsResponse(Promotion promotion) {
        return PromotionDetailsResponseDto.builder()
                .name(promotion.getName())
                .description(promotion.getDescription())
                .startDatetime(promotion.getStartDatetime())
                .endDatetime(promotion.getEndDatetime())
                .maxDiscount(promotion.getMaxDiscount())
                .maxUserApplications(promotion.getMaxUserApplications() != null ? promotion.getMaxUserApplications() : 0)
                .slug(promotion.getSlug())
                .state(promotion.getState())
                .tnc(promotion.getTnc())
                .type(promotion.getType())
                .promotionBenefits(getPromotionBenefits(promotion.getPromotionBenefits()))
                .promotionCreative(getPromotionCreative(promotion.getPromotionCreative()))
                .promotionCondition(getPromotionCondition(promotion.getPromotionCondition()))
                .promotionLimit(getPromotionLimit(promotion.getPromotionLimit()))
                .offerResponseDtoList(getOfferResponseDtoList(promotion))
                .build();
    }

    private PromotionBenefitDto getPromotionBenefits(PromotionBenefit promotionBenefit) {
        return PromotionBenefitDto.builder()
                .value(promotionBenefit.getValue())
                .build();
    }

    private PromotionCreativeDto getPromotionCreative(PromotionCreative promotionCreative) {
        return (promotionCreative == null) ? PromotionCreativeDto.builder().build() :
                PromotionCreativeDto.builder()
                        .banner(promotionCreative.getBanner())
                        .teaser1(promotionCreative.getTeaser1())
                        .teaser2(promotionCreative.getTeaser2())
                        .bannerImageUrl(promotionCreative.getBannerImageUrl())
                        .teaserImageUrl(promotionCreative.getTeaserImageUrl())
                        .build();
    }

    private PromotionConditionDto getPromotionCondition(PromotionCondition promotionCondition) {
        return PromotionConditionDto.builder()
                .value(promotionCondition.getValue())
                .type(promotionCondition.getType())
                .build();
    }

    private PromotionLimitDto getPromotionLimit(PromotionLimit promotionLimit) {
        return promotionLimit != null ? PromotionLimitDto.builder()
                .limitPerCustomer(promotionLimit.getLimitPerCustomer())
                .totalLimit(promotionLimit.getMaxCount())
                .build() : PromotionLimitDto.builder().build();
    }

    private List<OfferResponseDto> getOfferResponseDtoList(Promotion promotion) {
        return promotionOfferMapRepository.findByPromotion_Id(promotion.getId()).stream()
                .map(promotionOfferMap -> OfferResponseDto.builder()
                        .offerId(promotionOfferMap.getOffer().getId())
                        .build()).toList();
    }
}