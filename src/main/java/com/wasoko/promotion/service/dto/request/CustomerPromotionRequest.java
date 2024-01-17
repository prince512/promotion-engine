package com.wasoko.promotion.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

/**
 * The type Serviceability request.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPromotionRequest {

    @JsonProperty("serviceability_area_id")
    @UUID(allowNil = false, letterCase = UUID.LetterCase.INSENSITIVE, message = "Given serviceability area is not a valid UUID")
    @NotNull
    private String serviceabilityAreaId;

    @NotNull
    @JsonProperty("customer_id")
    @UUID(allowNil = false, letterCase = UUID.LetterCase.INSENSITIVE, message = "Given customer is not a valid UUID")
    private String customerId;

}
