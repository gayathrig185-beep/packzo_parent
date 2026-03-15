package com.ecommerce.packzo.response;

public record VariantDto(
        Long variantId,
        String modelName,
        Double additionalPrice
) {}