package com.ecommerce.packzo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name= "category_product_type_map", schema="packzodev")
@Data
public class CategoryProductTypeMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Category category;

    @ManyToOne
    private ProductType productType;
}
