package com.wasoko.promotion.service.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.wasoko.promotion.service.common.OfferValidationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import lombok.Setter;

/**
 * The type Offer request.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@OfferValidationRequest
@Setter
@Builder
public class PromotionRequest {

    @JsonProperty("offers")
    @NotEmpty
    @NotNull
    @Valid
    private List<Offer> offers;
}
