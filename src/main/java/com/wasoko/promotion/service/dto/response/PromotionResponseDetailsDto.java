package com.wasoko.promotion.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * The type Promotion response details dto.
 */
@Builder
@Data
public class PromotionResponseDetailsDto {

    @JsonProperty("id")
    private Long promotionId;
    @JsonProperty("name")
    private String promotionName;
    @JsonProperty("type")
    private String promotionType;
    @JsonProperty("discounted_price")
    private double discountedPrice;
    @JsonProperty("end_time")
    private LocalDateTime endTime;
    @JsonProperty("discount")
    private double discount;
    @JsonProperty("discount_percentage")
    private String discountPercentage;
}