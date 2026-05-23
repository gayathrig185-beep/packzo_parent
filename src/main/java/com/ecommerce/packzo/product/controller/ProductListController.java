/*
package com.ecommerce.packzo.product.controller;

import com.ecommerce.packzo.product.service.impl.ProductServiceImpl;
import com.ecommerce.packzo.product.service.interfaces.ProductService;
import com.ecommerce.packzo.response.ProductDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductListController {

    private final ProductService productService;

    public ProductListController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailDto> getProductDetail(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.getProductDetail(id)
        );
    }
}
*/
