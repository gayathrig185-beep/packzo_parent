package com.ecommerce.packzo.product.controller;

import com.ecommerce.packzo.product.service.impl.ProductServiceImpl;
import com.ecommerce.packzo.product.service.interfaces.ProductService;
import com.ecommerce.packzo.response.ProductDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductListController {

    private final ProductService productService;

    public ProductListController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailDto> getProductDetail(
            @PathVariable Long productId, @RequestParam String categoryId) {

        return ResponseEntity.ok(
                productService.getProductDetails(productId, categoryId)
        );
    }
}
