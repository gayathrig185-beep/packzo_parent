package com.ecommerce.packzo.product.service.interfaces;

import com.ecommerce.packzo.response.ProductDto;
import com.ecommerce.packzo.response.ProductSuggestionDto;
import com.ecommerce.packzo.response.ProductSuggestionResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SearchProductService {

    List<ProductDto> searchProducts(String keyword, String categoryId);
    ProductSuggestionResponseDto getSuggestions(String keyword, String categoryId);
}
