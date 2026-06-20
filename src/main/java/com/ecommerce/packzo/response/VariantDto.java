package com.ecommerce.packzo.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VariantDto{
    private String variantId;
    private String variantName;
    private String capacityMl;
    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private Integer noOfPieces;
    private Double discountPercentage;
    private BigDecimal pricePerPiece;
    private String origin;
    private String dimension;
    private String color;
    private String units;
    private String keyfeatures;
    private String description;
    private String disclaimer;
    private String careDetails;
    private String returnPolicy;
    private String marketNameAndAddress;
    private String sellerAddress;
    private String material;
    private Long qunatity;
}

