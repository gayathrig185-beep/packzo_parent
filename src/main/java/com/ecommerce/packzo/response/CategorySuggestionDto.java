package com.ecommerce.packzo.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CategorySuggestionDto(

        String categoryId,

        String categoryName,

        List<ProductSuggestionDto> products
) {
}