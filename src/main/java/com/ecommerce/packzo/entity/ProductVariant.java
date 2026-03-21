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
    private int quantity;
    private String material;
    private String feature;
    private String color;
    private int capacity;
    @Column(columnDefinition = "TEXT")
    private String aboutItem;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private Integer units;
    private String countryOfOrigin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}