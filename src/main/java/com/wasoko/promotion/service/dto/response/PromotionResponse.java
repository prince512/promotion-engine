package com.wasoko.promotion.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * The type Promotion response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponse {

    @JsonProperty("id")
    private Long promotionId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("start_time")
    private LocalDateTime startTime;
    @JsonProperty("end_time")
    private LocalDateTime endTime;
    @JsonProperty("teaser1")
    private String teaser1;
    @JsonProperty("teaser2")
    private String teaser2;
    @JsonProperty("banner_image_url")
    private String bannerImageUrl;
    @JsonProperty("teaser_image_url")
    private String teaserImageUrl;
    @JsonProperty("tnc")
    private String tnc;
    @JsonProperty("promotion_condition")
    private PromotionConditionDto promotionConditionDto;
    @JsonProperty("promotion_limit")
    private PromotionLimitDto promotionLimitDto;

}
