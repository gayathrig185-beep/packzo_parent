package com.ecommerce.packzo.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailDto(
        Long productId,
        String productName,
        String categoryId,
        List<VariantGroupDto> variantGroups
) {}