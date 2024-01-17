package com.wasoko.promotion.service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Promotion benefit.
 */
@Data
@Entity
@Table(name = "promotion_benefit")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionBenefit {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "type", nullable = false, length = 128)
    private String type;

    @Column(name = "value", precision = 12, scale = 2)
    private Float value;

    @OneToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

}