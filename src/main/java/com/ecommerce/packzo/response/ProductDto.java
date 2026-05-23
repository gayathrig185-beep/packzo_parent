package com.ecommerce.packzo.response;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String name,
        BigDecimal originalPrice,
        BigDecimal discountPrice,
        Integer ratings,
        String percentage
) {}
