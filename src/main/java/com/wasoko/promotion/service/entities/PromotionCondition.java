package com.wasoko.promotion.service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The type Promotion condition.
 */
@Getter
@Setter
@Entity
@Table(name = "promotion_promotioncondition")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionCondition {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "type", nullable = false, length = 128)
    private String type;

    @Column(name = "value")
    private Integer value;

    @OneToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

}