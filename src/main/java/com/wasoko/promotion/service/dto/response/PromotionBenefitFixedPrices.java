package com.wasoko.promotion.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Promotion benefit fixed prices.
 */
@Builder
public class PromotionBenefitFixedPrices implements Serializable {

    @Serial
    private static final long serialVersionUID = -5985130747945257259L;

    @JsonProperty("offer_id")
    private Long offerId;

    @JsonProperty("fixed_price")
    private Float fixedPrice;

}