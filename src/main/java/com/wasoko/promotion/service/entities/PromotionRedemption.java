package com.wasoko.promotion.service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import lombok.NoArgsConstructor;

/**
 * The type Promotion redemption.
 */
@Data
@Entity
@Table(name = "promotion_promotionredemption")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRedemption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", columnDefinition = "uuid")
    private UUID customerId;

    @Column(name = "offer_id")
    private Long offerId;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "discount_availed", precision = 10, scale = 2)
    private Float discountAvailed;

    @Column(name = "order_id", columnDefinition = "uuid")
    private UUID orderId;

}
