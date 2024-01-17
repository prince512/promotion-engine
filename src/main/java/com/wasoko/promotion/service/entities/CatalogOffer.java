package com.wasoko.promotion.service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The type Catalog offer.
 */
@Getter
@Setter
@Entity
@Table(name = "catalog_offer")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CatalogOffer {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private Float price;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = false;

}