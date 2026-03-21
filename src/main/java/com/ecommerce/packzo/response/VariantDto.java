package com.ecommerce.packzo.response;

import java.math.BigDecimal;

public record VariantDto(
        String variantId,
        String variantName,
        Integer capacityMl,
        BigDecimal bottomSize,
        BigDecimal originalPrice,
        BigDecimal discountPrice,
        Integer noOfPieces,
        Double discountPercentage,
        BigDecimal pricePerPiece

) {}