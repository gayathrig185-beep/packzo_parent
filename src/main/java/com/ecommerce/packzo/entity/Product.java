package com.ecommerce.packzo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "product" , schema="packzodev")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long productId;
    private String productName;
    private String productShortDescription;
    private String productLongDescription;
    private int quantity;
    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private String material;
    private String feature;
    private String color;
    private String capacity;
    private Double rating;
    private Integer totalRatings;
    private boolean isActive;
    @Column(columnDefinition = "TEXT")
    private String aboutItem;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productCategory_productCategoryId")
    private ProductCategory productCategory;
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductVariant> variants;

}



