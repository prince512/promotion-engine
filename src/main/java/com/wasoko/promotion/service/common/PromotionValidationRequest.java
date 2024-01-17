package com.wasoko.promotion.service.common;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * The interface Promotion Validation Request.
 *
 * @author Prince
 */
@Documented
@Constraint(validatedBy = {PromotionRequestValidator.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.FIELD,
        ElementType.TYPE,
        ElementType.ANNOTATION_TYPE
})
public @interface PromotionValidationRequest {

    /**
     * Message string.
     *
     * @return the string
     * @author Prince
     */
    String message() default "Offer request is not valid";

    /**
     * Groups class [ ].
     *
     * @return the class [ ]
     * @author Prince
     */
    Class<?>[] groups() default {};

    /**
     * Payload class [ ].
     *
     * @return the class [ ]
     * @author Prince
     */
    Class<? extends Payload>[] payload() default {};
}
