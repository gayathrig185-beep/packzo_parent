package com.ecommerce.packzo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDto {

    private String variantId;

    private Integer pieces;

    private BigDecimal price;     // discount price

    private BigDecimal mrp;

    private boolean inStock;
}