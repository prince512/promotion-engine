package com.wasoko.promotion.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Promotion creative.
 */
@Builder
public class PromotionCreativeDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -5985130747945257259L;

    @JsonProperty("banner")
    private String banner;
    @JsonProperty("teaser1")
    private String teaser1;
    @JsonProperty("teaser2")
    private String teaser2;
    @JsonProperty("banner_image_url")
    private String bannerImageUrl;
    @JsonProperty("teaser_image_url")
    private String teaserImageUrl;
}