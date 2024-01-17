package com.wasoko.promotion.service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Promotion benefit fixed price.
 */
@Getter
@Setter
@Entity
@Table(name = "promotion_benefitfixedprice")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionBenefitFixedPrice {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "fixed_price", nullable = false, precision = 12, scale = 2)
    private Float fixedPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offer_id", nullable = false)
    private CatalogOffer offer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

}