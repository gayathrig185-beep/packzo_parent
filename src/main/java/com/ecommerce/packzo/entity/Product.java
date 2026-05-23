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
    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private Double rating;
    private Integer totalRatings;
    private boolean isActive;
    private String customerCare;
    private String manfacturersAddress;
    private String sellerAddress;
    private String countryOfOrigin;
    private String returnPolicy;
    private String careDetails;
    /*@ManyToMany
    @JoinTable(name = "product_category_mapping", schema="packzodev", joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "category_id"))
    @JoinColumn(name = "category_id")
    private List<Category> categories;
*/
    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;
    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;*/
}



