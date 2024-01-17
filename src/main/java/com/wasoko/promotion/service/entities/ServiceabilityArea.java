package com.wasoko.promotion.service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * The persistent class for the serviceabilityarea database table.
 */
@Entity
@Data
@Table(name = "catalog_serviceabilityarea")
public class ServiceabilityArea {

    @Id
    private String id;

    private String name;

}