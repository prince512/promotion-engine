package com.wasoko.promotion.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Promotion redemption list.
 */
@Builder
public class PromotionRedemptionListDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -5985130747945257259L;

    @JsonProperty("customer_id")
    private String customerId;
    @JsonProperty("offer_id")
    private Long offerId;
    @JsonProperty("quantity")
    private int quantity;
    @JsonProperty("discount_availed")
    private Float discountAvailed;
    @JsonProperty("order_id")
    private int orderId;
}