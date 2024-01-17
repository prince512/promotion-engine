package com.wasoko.promotion.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Redemption offer dto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionOfferDto {

    @NotNull
    @JsonProperty("offer_id")
    @Positive
    private Long offerId;

    @NotNull
    @JsonProperty("promotion_id")
    @Positive
    private Long promoId;

    @Min(1)
    @JsonProperty("qty")
    @Positive
    private Integer quantity;

    @NotNull
    @Positive
    @JsonProperty("discount_availed")
    private Float discountAvailed;

}
