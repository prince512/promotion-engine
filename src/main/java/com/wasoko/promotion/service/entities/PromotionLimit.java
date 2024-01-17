package com.wasoko.promotion.service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Promotion limit.
 */
@Getter
@Setter
@Entity
@Table(name = "promotion_promotionlimit")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "limit_per_customer", nullable = false)
    private Integer limitPerCustomer;

    @Column(name = "max_count", nullable = false)
    private Integer maxCount;

    // don't use this column unless needed
    @OneToOne
    @JoinColumn(name = "offer_id", nullable = false)
    private CatalogOffer offer;

    // don't use this column unless needed
    @OneToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

}