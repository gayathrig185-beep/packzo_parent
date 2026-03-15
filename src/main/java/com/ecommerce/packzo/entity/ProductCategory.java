package com.ecommerce.packzo.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "productCategory" , schema = "packzodev")
public class ProductCategory {

    @Id
    private String productCategoryId;
    private String prdCategoryName;
    private String prdCategoryDescription;
    private boolean isActive;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_sectorId")
    private Sector sector;
}

