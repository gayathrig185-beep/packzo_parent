package com.ecommerce.packzo.request;

import lombok.Data;

@Data
public class AddCartRequest {

    private String productVariantId;

    private Integer quantity;
}