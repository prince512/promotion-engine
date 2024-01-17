package com.wasoko.promotion.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wasoko.promotion.service.common.PromotionValidationRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import lombok.Setter;

/**
 * The type Promotion validation request.
 */
@AllArgsConstructor
@NoArgsConstructor
@PromotionValidationRequest
@Getter
@Setter
@Builder
public class PromotionValidationRequestDto extends CustomerPromotionRequest {

    @NotNull
    @NotEmpty
    @JsonProperty("offers")
    private ArrayList<OfferDetails> offerDetails;
}