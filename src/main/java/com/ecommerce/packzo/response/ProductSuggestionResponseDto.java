package com.ecommerce.packzo.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductSuggestionResponseDto(

        String searchScope,

        String keyword,

        List<ProductSuggestionDto> products,

        Boolean showSearchAllCategories,

        String message,

        List<CategorySuggestionDto> otherCategorySuggestions
) {
}