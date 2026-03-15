package com.ecommerce.packzo.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailDto(
        Long productId,
        String productName,
        String shortDescription,
        String longDescription,
        BigDecimal originalPrice,
        BigDecimal discountPrice,
        Double rating,
        Integer totalRatings,
        String material,
        String feature,
        String color,
        String capacity,
        String aboutItem,
        String categoryName,
        String sectorName,
        List<VariantDto> variants
) {}