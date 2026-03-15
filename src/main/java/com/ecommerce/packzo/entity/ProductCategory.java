package com.ecommerce.packzo.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "productCategory")
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String productCategoryId;
    private String prdCategoryName;
    private String prdCategoryDescripion;
    private boolean isActive;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_sectorId")
    private Sector sector;
}

