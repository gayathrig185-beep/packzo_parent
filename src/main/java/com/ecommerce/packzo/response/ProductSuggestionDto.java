package com.ecommerce.packzo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSuggestionDto {

    private Long productId;

    private String productName;

    private String imageUrl;
}