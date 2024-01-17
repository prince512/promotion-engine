package com.wasoko.promotion.service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The type Promotion offer map.
 */
@Getter
@Setter
@Entity
@Table(name = "promotion_promotionofferdump")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionOfferMap {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "offer_id", nullable = false)
    private CatalogOffer offer;

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

}