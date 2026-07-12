package com.ecommerce.packzo.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartItemDto {

    private Long cartId;

    private Long productId;

    private String productVariantId;

    private String productName;

    private String variantName;

    private String category;

    private String sector;

    private String thumbnailUrl;

    private Integer quantity;

    private String capacity;

    private Integer noOfPieces;

    private String imageUrl;

    private Boolean outOfStock;
}