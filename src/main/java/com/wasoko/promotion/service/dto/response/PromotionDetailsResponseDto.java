package com.wasoko.promotion.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

/**
 * The type PromotionDetailsResponseDto.
 */
@Builder
@Getter
public class PromotionDetailsResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -5985130747945257259L;

    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("start_date_time")
    private LocalDateTime startDatetime;
    @JsonProperty("end_date_time")
    private LocalDateTime endDatetime;
    @JsonProperty("max_discount")
    private Float maxDiscount;
    @JsonProperty("max_user_applications")
    private int maxUserApplications;
    @JsonProperty("slug")
    private String slug;
    @JsonProperty("state")
    private String state;
    @JsonProperty("tnc")
    private String tnc;
    @JsonProperty("type")
    private String type;
    @JsonProperty("benefits")
    private PromotionBenefitDto promotionBenefits;
    @JsonProperty("creative")
    private PromotionCreativeDto promotionCreative;
    @JsonProperty("condition")
    private PromotionConditionDto promotionCondition;
    @JsonProperty("limit")
    private PromotionLimitDto promotionLimit;
    @JsonProperty("offers")
    private List<OfferResponseDto> offerResponseDtoList;

}