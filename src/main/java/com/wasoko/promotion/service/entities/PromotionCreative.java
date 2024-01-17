package com.wasoko.promotion.service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Promotion creative.
 */
@Getter
@Setter
@Entity
@Table(name = "promotion_creative")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionCreative {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "banner", length = 100)
    private String banner;
    @Column(name = "teaser_1")
    private String teaser1;
    @Column(name = "teaser_2")
    private String teaser2;
    @Column(name = "banner_image_url")
    private String bannerImageUrl;
    @Column(name = "teaser_image_url")
    private String teaserImageUrl;

}