package com.wasoko.promotion.service.common;

import com.wasoko.promotion.service.dto.request.OfferDetails;
import com.wasoko.promotion.service.dto.request.PromotionValidationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * The type Promotion Validation Request.
 */
public class PromotionRequestValidator implements ConstraintValidator<PromotionValidationRequest, Object> {


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

        final ArrayList<OfferDetails> offerDetails = ((PromotionValidationRequestDto) value).getOfferDetails();
        Set<Long> offerIds = offerDetails.stream()
                .map(OfferDetails::getOfferId)
                .collect(Collectors.toSet());

        if (offerIds.size() != offerDetails.size()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "There is a Duplicate Offer present in the promotion validation Request. Please provide unique offers.")
                    .addPropertyNode("offerDetails")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

