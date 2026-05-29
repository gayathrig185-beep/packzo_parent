package com.ecommerce.packzo.product.controller;

import com.ecommerce.packzo.product.service.interfaces.SearchProductService;
import com.ecommerce.packzo.response.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchProductsController {


    private final SearchProductService service;

    public SearchProductsController(SearchProductService service) {
        this.service = service;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String keyword, @RequestParam String categoryId) {

        return ResponseEntity.ok(
                service.searchProducts(keyword,categoryId));
}}
