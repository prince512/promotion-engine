package com.wasoko.promotion.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Promotion offer details response.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferPromotionDetailsResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -5985130747945257259L;
    @JsonProperty("id")
    private long offerId;
    @JsonProperty("original_price")
    private double originalPrice;
    @JsonProperty("promotions")
    private PromotionResponseDetailsDto promotions;


}
