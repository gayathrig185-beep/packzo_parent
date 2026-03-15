package com.ecommerce.packzo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "productVariant")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long variantId;

    private String modelName;
    private Double additionalPrice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}