package com.ecommerce.packzo.product.service.impl;

import com.ecommerce.packzo.entity.Category;
import com.ecommerce.packzo.entity.Product;
import com.ecommerce.packzo.entity.ProductType;
import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.product.repository.CategoryRepository;
import com.ecommerce.packzo.product.repository.ProductRepository;
import com.ecommerce.packzo.product.service.interfaces.SearchProductService;
import com.ecommerce.packzo.response.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchProductServiceImpl implements SearchProductService {

    private static final Logger logger = LoggerFactory.getLogger(SearchProductServiceImpl.class);

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepository;

    public List<ProductDto> searchProducts(String keyword, String categoryId) {
        List<Product> products = new ArrayList<>();
        //Pageable pageable = PageRequest.of(page, size);
        if (categoryId.equalsIgnoreCase("all")) {
            products = productRepo.globalSearchProducts(keyword, categoryId);
        } else {
            products = productRepo.globalSearchProductsByCategory(keyword, categoryId);
            if(products.isEmpty()){
                products = productRepo.globalSearchProducts(keyword,categoryId);
            }
        }
        return mapToDto(products);
    }

    private List<ProductDto> mapToDto(List<Product> product) {
        // Default value if type is null
        return product.stream()
                .filter(Objects::nonNull)
                .map(prd -> {
                    String typeName = Optional.ofNullable(prd.getProductType())
                            .map(ProductType::getTypeName)
                            .orElse("Unknown"); // Default value if type is null

                    return new ProductDto(
                            prd.getProductId(),
                            prd.getProductName(),
                            typeName,
                            prd.getOriginalPrice(),
                            prd.getDiscountPrice(),
                            prd.getTotalRatings(),
                            "",
                            prd.getQuantity()
                    );
                }).toList();
    }

/*
    public List<ProductSuggestionDto> getSuggestions(String keyword, String categoryId) {

        try {

            if (keyword == null || keyword.isBlank()) {
                return Collections.emptyList();
            }

            Pageable pageable = PageRequest.of(0, 5);

            return productRepo
                    .findSuggestions(keyword.trim(), pageable)
                    .getContent()
                    .stream()
                    .map(this::mapToSuggestionDto)
                    .toList();

        } catch (Exception ex) {

            logger.error("Error while fetching suggestions", ex);

            throw new PaczoException("", "", "Failed to fetch product suggestions");
        }
    }

    private ProductSuggestionDto mapToSuggestionDto(
            Product product) {

        return ProductSuggestionDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .imageUrl(product.getThumbnail())
                .build();
    }*/


    public ProductSuggestionResponseDto getSuggestions(String keyword, String categoryId) {

        try {
            Pageable pageable = PageRequest.of(0, 5);
            if (categoryId.equalsIgnoreCase("all")) {
                List<ProductSuggestionDto> productsSuggestionList = productRepo.findSuggestions(keyword.trim(), pageable)
                        .getContent()
                        .stream()
                        .map(this::mapToDto)
                        .toList();
                return ProductSuggestionResponseDto.builder()
                        .searchScope(categoryId)
                        .keyword(keyword)
                        .products(productsSuggestionList)
                        .showSearchAllCategories(true)
                        .build();
            } else {
                List<ProductSuggestionDto> productsSuggestionList = productRepo.findSuggestionsByCategory(keyword, categoryId, pageable)
                        .stream()
                        .map(this::mapToDto)
                        .toList();


                if (!productsSuggestionList.isEmpty()) {

                    return ProductSuggestionResponseDto.builder()
                            .searchScope(categoryId)
                            .keyword(keyword)
                            .products(productsSuggestionList)
                            .showSearchAllCategories(false)
                            .build();
                } else {
                    List<CategorySuggestionDto> otherCategories = buildOtherCategorySuggestions(keyword, categoryId);
                    Category category = categoryRepository.findByCategoryId(categoryId);

                    return ProductSuggestionResponseDto.builder()
                            .searchScope(categoryId)
                            .keyword(keyword)
                            .products(Collections.emptyList())
                            .showSearchAllCategories(true)
                            .message("No result for " + keyword + " in " + category.getCategoryName())
                            .otherCategorySuggestions(otherCategories)
                            .build();
                }
            }

        } catch (Exception ex) {

            logger.error("Error while searching suggestions", ex);

            throw new PaczoException("", "", "Failed to fetch suggestions");
        }
    }


    private List<CategorySuggestionDto> buildOtherCategorySuggestions(String keyword, String selectedCategoryId) {

        Pageable pageable = PageRequest.of(0, 5);

        List<Product> products = productRepo.findProductsInOtherCategories(keyword, selectedCategoryId, pageable);

        Category category = categoryRepository.findByCategoryId(selectedCategoryId);


        return Collections.singletonList(CategorySuggestionDto.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .products(products.stream()
                        .limit(5)
                        .map(this::mapToDto)
                        .toList()).build());

    }

    private ProductSuggestionDto mapToDto(Product product) {

        return ProductSuggestionDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .imageUrl(product.getImageUrl())
                .build();
    }

}