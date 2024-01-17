package com.wasoko.promotion.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * The type Offer promotion dto.
 */
@Data
@Builder
public class OfferPromotionDto {
    @JsonProperty("offer_id")
    private Long offerId;
    @JsonProperty("id")
    private Long promoId;
    @JsonProperty("original_price")
    private double originalPrice;
    @JsonProperty("promotional_price")
    private double discountedPrice;
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String type;
    @JsonProperty("calculated_discount")
    private double calculatedDiscount;
    @JsonProperty("calculated_discount_percentage")
    private String calculatedDiscountPercentage;
    @JsonProperty("end_time")
    private String endDate;
}