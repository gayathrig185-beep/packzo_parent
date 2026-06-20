package com.ecommerce.packzo.product.controller;

import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.product.service.interfaces.SearchProductService;
import com.ecommerce.packzo.response.ProductDto;
import com.ecommerce.packzo.response.ProductSuggestionDto;
import com.ecommerce.packzo.response.ProductSuggestionResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(SearchProductsController.class);

    private final SearchProductService searchService;

    public SearchProductsController(SearchProductService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String keyword, @RequestParam String categoryId) {

        return ResponseEntity.ok(
                searchService.searchProducts(keyword, categoryId));
    }

    @GetMapping("/suggestions")
    public ProductSuggestionResponseDto getSuggestions(
            @RequestParam String keyword, @RequestParam(required = false) String categoryId) {


        return searchService.getSuggestions(keyword,categoryId);

    }

}
