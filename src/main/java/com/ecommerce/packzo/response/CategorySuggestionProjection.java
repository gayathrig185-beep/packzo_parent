package com.ecommerce.packzo.response;

import lombok.Data;

@Data
public class CategorySuggestionProjection {

    private String categoryId;

    private String categoryName;

    private Long matchedProducts;
}