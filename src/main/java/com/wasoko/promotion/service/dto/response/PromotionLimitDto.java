package com.wasoko.promotion.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Promotion limit.
 */
@Builder
public class PromotionLimitDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -5985130747945257259L;

    @JsonProperty("limit_per_customer")
    private Integer limitPerCustomer;
    @JsonProperty("total_limit")
    private Integer totalLimit;
}