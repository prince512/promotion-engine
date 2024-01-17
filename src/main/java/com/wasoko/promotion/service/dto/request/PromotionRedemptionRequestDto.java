package com.wasoko.promotion.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wasoko.promotion.service.common.PromotionRedemptionValidationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

import java.util.List;

/**
 * The type Promotion redemption request dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PromotionRedemptionValidationRequest
public class PromotionRedemptionRequestDto {

    @NotNull
    @JsonProperty("customer_id")
    @UUID(allowNil = false, letterCase = UUID.LetterCase.INSENSITIVE, message = "Given customerId is not a valid UUID")
    private String customerId;

    @NotNull
    @JsonProperty("order_id")
    @UUID(allowNil = false, letterCase = UUID.LetterCase.INSENSITIVE, message = "Given orderId is not a valid UUID")
    private String orderId;

    @NotNull
    @Valid
    @NotEmpty
    @JsonProperty("offers")
    private List<RedemptionOfferDto> offers;

}
