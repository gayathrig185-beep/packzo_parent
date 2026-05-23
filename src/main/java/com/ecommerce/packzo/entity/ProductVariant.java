package com.ecommerce.packzo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "productVariant" , schema="packzodev")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String variantId;
    private String variantName;
    private int noOfPieces;
    private String material;
    private String feature;
    private String description;
    private String color;
    private String capacity;
    private String disclaimer;
    private String dimensions;
    @Column(columnDefinition = "TEXT")
    private String aboutItem;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private String units;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}