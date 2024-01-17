package com.wasoko.promotion.service.common;

import com.wasoko.promotion.service.dto.request.Offer;
import com.wasoko.promotion.service.dto.request.PromotionRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type Offer request validator.
 */
public class OfferRequestValidator implements ConstraintValidator<OfferValidationRequest, Object> {


    /**
     * Is valid boolean.
     *
     * @param value   the value
     * @param context the context
     * @return the boolean
     * @author Prince
     */

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        final List<Offer> offers = ((PromotionRequest) value).getOffers();

        Set<Long> offerIds = offers.stream()
                .map(Offer::getOfferId)
                .collect(Collectors.toSet());

        if (offerIds.size() != offers.size()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "There is a Duplicate Offer present in the promotion validation Request. Please provide unique offers.")
                    .addPropertyNode("offers")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

