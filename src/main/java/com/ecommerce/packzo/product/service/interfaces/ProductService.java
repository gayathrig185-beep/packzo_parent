package com.ecommerce.packzo.product.service.interfaces;

import com.ecommerce.packzo.response.ProductDetailDto;


public interface ProductService {

    ProductDetailDto getProductDetails(Long productId, String categoryId);
}

