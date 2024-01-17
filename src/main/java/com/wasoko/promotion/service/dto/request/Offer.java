package com.wasoko.promotion.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * The type Offer.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Offer {

    @JsonProperty("id")
    @NotNull
    @Positive(message = "Offer Id should be positive")
    private Long offerId;

    @JsonProperty("original_price")
    @Positive(message = "original price should be positive")
    private double originalPrice;
}
