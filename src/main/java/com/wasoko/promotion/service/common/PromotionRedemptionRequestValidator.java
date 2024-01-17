package com.wasoko.promotion.service.common;

import com.wasoko.promotion.service.dto.request.PromotionRedemptionRequestDto;
import com.wasoko.promotion.service.dto.request.RedemptionOfferDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Promotion redemption request validator.
 */
public class PromotionRedemptionRequestValidator implements ConstraintValidator<PromotionRedemptionValidationRequest, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        final List<RedemptionOfferDto> redemptionOfferDtoList = ((PromotionRedemptionRequestDto) value).getOffers();

        List<RedemptionOfferDto> uniqueOffers = redemptionOfferDtoList.stream()
                .collect(Collectors.toMap(RedemptionOfferDto::getOfferId, dto -> dto, (dto1, dto2) -> dto1))
                .values()
                .stream()
                .toList();

        if (uniqueOffers.size() != redemptionOfferDtoList.size()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Failed to save promotions: Duplicate offer IDs in the request. Please provide unique offers.")
                    .addPropertyNode("offers")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
