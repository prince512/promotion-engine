package com.wasoko.promotion.service.service.impl;

import com.wasoko.promotion.service.aop.RootResponseDTO;
import com.wasoko.promotion.service.dto.request.PromotionRedemptionRequestDto;
import com.wasoko.promotion.service.dto.request.RedemptionOfferDto;
import com.wasoko.promotion.service.entities.Promotion;
import com.wasoko.promotion.service.entities.PromotionRedemption;
import com.wasoko.promotion.service.exception.InvalidPromotionException;
import com.wasoko.promotion.service.repository.PromotionRedemptionRepository;
import com.wasoko.promotion.service.repository.PromotionRepository;
import com.wasoko.promotion.service.service.PromotionRedemptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Promotion redemption service.
 */
@Service
@Slf4j
public class PromotionRedemptionServiceImpl implements PromotionRedemptionService {

    private final PromotionRedemptionRepository promotionRedemptionRepository;
    private final PromotionRepository promotionRepository;

    /**
     * Instantiates a new Promotion redemption service.
     *
     * @param promotionRedemptionRepository the promotion redemption repository
     * @param promotionRepository           the promotion repository
     */
    @Autowired
    public PromotionRedemptionServiceImpl(PromotionRedemptionRepository promotionRedemptionRepository,
                                          PromotionRepository promotionRepository) {
        this.promotionRedemptionRepository = promotionRedemptionRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    public RootResponseDTO applyRedemption(PromotionRedemptionRequestDto request) {
        List<RedemptionOfferDto> redemptionDTOList = request.getOffers();
        List<PromotionRedemption> redemptionList = new ArrayList<>();
        log.info("Processing PromotionRedemptionRequestDTO: {}", request);

        for (RedemptionOfferDto redemptionDTO : redemptionDTOList) {
            PromotionRedemption redemption = new PromotionRedemption();
            redemption.setCustomerId(UUID.fromString(request.getCustomerId()));
            redemption.setOrderId(UUID.fromString(request.getOrderId()));
            redemption.setOfferId(redemptionDTO.getOfferId());

            Optional<Promotion> promotion = promotionRepository.findById(redemptionDTO.getPromoId());
            if (promotion.isEmpty() || redemptionDTO.getPromoId() == null) {
                String errorMessage = "One or more invalid promotion IDs were provided; they are not found in the database.";
                log.error("Error occurred while processing PromotionRedemptionRequestDTO: {}. Reason: {}", request, errorMessage);
                throw new InvalidPromotionException(errorMessage);
            }
            promotion.ifPresent(redemption::setPromotion);
            redemption.setQuantity(redemptionDTO.getQuantity());
            redemption.setDiscountAvailed(redemptionDTO.getDiscountAvailed());

            redemptionList.add(redemption);
        }

        promotionRedemptionRepository.saveAll(redemptionList);

        return new RootResponseDTO("Redemption saved successfully");
    }
}