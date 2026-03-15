package com.ecommerce.packzo.user.product.service.interfaces;

import com.ecommerce.packzo.response.ProductDetailDto;

public interface ProductService {
    ProductDetailDto getProductDetail(Long productId);

}
