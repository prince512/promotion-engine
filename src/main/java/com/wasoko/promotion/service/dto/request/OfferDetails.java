package com.wasoko.promotion.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * The type Offer details.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferDetails {

    @JsonProperty("id")
    @NotNull
    private Long offerId;
    @JsonProperty("price")
    @NotNull
    @Positive
    private double price;
    @JsonProperty("quantity")
    @NotNull
    @Min(1)
    private Integer quantity;
}