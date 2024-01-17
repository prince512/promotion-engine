package com.wasoko.promotion.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Promotion condition.
 */
@Builder
public class PromotionConditionDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -5985130747945257259L;

    @JsonProperty("type")
    private String type;
    @JsonProperty("value")
    private Integer value;
}