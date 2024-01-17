package com.wasoko.promotion.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * The type Offer promotion response.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class OfferPromotionResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -5985130747945257259L;

    /**
     * The Offers.
     */
    @JsonProperty("offers")
    private List<OfferPromotionDto> offerPromotionDtoList;

}