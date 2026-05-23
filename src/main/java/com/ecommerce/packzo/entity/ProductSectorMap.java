package com.ecommerce.packzo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_sector_map", schema = "packzodev")
@Getter
@Setter
public class ProductSectorMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private ProductType productType;

    @ManyToOne
    @JoinColumn(name = "sector_id")
    private Sector sector;
}