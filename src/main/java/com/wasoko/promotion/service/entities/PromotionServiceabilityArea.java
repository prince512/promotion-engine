package com.wasoko.promotion.service.entities;

import jakarta.persistence.*;
import lombok.Data;

/**
 * The type Promotion serviceability area.
 */
@Entity
@Data
@Table(name = "promotion_promotion_serviceability_areas")
public class PromotionServiceabilityArea {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "serviceabilityarea_id")
    private ServiceabilityArea serviceabilityArea;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;


}