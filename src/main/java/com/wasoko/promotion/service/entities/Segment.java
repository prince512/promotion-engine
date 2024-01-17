package com.wasoko.promotion.service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Segment.
 */
@Getter
@Setter
@Entity
@Table(name = "promotion_segment")
public class Segment {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "segment", nullable = false)
    private String segmentName;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

}