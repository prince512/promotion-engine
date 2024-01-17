package com.wasoko.promotion.service.service.impl;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.common.util.PromotionConstraintsUtil;
import com.wasoko.promotion.service.dto.response.PromotionConditionDto;
import com.wasoko.promotion.service.dto.response.PromotionLimitDto;
import com.wasoko.promotion.service.dto.response.PromotionResponse;
import com.wasoko.promotion.service.entities.Promotion;
import com.wasoko.promotion.service.repository.PromotionRepository;
import com.wasoko.promotion.service.repository.PromotionSegmentRepository;
import com.wasoko.promotion.service.service.PromotionCustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The type Promotion customer service.
 */
@Service
@Slf4j
public class PromotionCustomerServiceImpl implements PromotionCustomerService {

    private final PromotionRepository promotionRepository;
    private final PromotionSegmentRepository promotionSegmentRepository;

    /**
     * Instantiates a new Promotion customer service.
     *
     * @param promotionRepository        the promotion repository
     * @param promotionSegmentRepository the promotion segment repository
     */
    @Autowired
    public PromotionCustomerServiceImpl(PromotionRepository promotionRepository, PromotionSegmentRepository promotionSegmentRepository) {
        this.promotionRepository = promotionRepository;
        this.promotionSegmentRepository = promotionSegmentRepository;
    }

    @Override
    public RootResponseDTO getPromotionsForCustomerAndServiceabilityArea(
            String serviceabilityAreaId,
            String customerId) {
        log.info("Received request to get promotions for serviceabilityAreaId: {} and customerId: {}", serviceabilityAreaId, customerId);
        List<Long> promotionsForCustomer = promotionSegmentRepository.findPromotionsByServiceabilityAreaIdAndCustomerID(
                UUID.fromString(serviceabilityAreaId), UUID.fromString(customerId));

        List<Promotion> promotions = promotionRepository.findAllByIdIn(promotionsForCustomer);

        List<PromotionResponse> validPromotions = promotions.stream()
                .filter(promotion -> PromotionConstraintsUtil.isPromotionValid(null, promotion, customerId))
                .map(promotion -> {
                    PromotionResponse.PromotionResponseBuilder promotionResponseBuilder = PromotionResponse.builder();
                    promotionResponseBuilder
                            .promotionId(promotion.getId())
                            .name(promotion.getName())
                            .description(promotion.getDescription())
                            .startTime(promotion.getStartDatetime())
                            .endTime(promotion.getEndDatetime());
                    if (promotion.getPromotionCreative() != null) {
                        promotionResponseBuilder
                                .teaser1(promotion.getPromotionCreative().getTeaser1())
                                .teaser2(promotion.getPromotionCreative().getTeaser2())
                                .bannerImageUrl(promotion.getPromotionCreative().getBannerImageUrl())
                                .teaserImageUrl(promotion.getPromotionCreative().getTeaserImageUrl());
                    }
                    promotionResponseBuilder
                            .tnc(promotion.getTnc())
                            .promotionLimitDto(PromotionLimitDto.builder()
                                    .totalLimit(promotion.getPromotionLimit() != null ? promotion.getPromotionLimit().getMaxCount() : 0)
                                    .limitPerCustomer(promotion.getPromotionLimit() != null ? promotion.getPromotionLimit().getLimitPerCustomer() : 0)
                                    .build())
                            .promotionConditionDto(PromotionConditionDto.builder()
                                    .value(promotion.getPromotionCondition().getValue())
                                    .type(promotion.getPromotionCondition().getType())
                                    .build());
                    return promotionResponseBuilder.build();
                }).toList();

        if (validPromotions.isEmpty()) {
            log.info("No promotions found for serviceabilityAreaId: {} and customerId: {}. Returning an empty response.",
                    serviceabilityAreaId, customerId);
            return new RootResponseDTO(new ArrayList<>());
        }

        return new RootResponseDTO(validPromotions);

    }

}