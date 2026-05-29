package com.ecommerce.packzo.product.service.interfaces;

import com.ecommerce.packzo.response.ProductDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SearchProductService {

    List<ProductDto> searchProducts(String keyword, String categoryId);
}
