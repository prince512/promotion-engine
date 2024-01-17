package com.wasoko.promotion.service.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Promotion segment.
 */
@Getter
@Setter
@Entity
@Table(name = "promotion_promotion_segments")
public class PromotionSegment {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "segment_id", nullable = false)
    private Segment segment;

}