package com.ecommerce.packzo.product.service.impl;

import com.ecommerce.packzo.entity.Product;
import com.ecommerce.packzo.entity.ProductType;
import com.ecommerce.packzo.product.repository.ProductRepository;
import com.ecommerce.packzo.product.service.interfaces.SearchProductService;
import com.ecommerce.packzo.response.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchProductServiceImpl implements SearchProductService {

    private final ProductRepository productRepo;

    public List<ProductDto> searchProducts(String keyword, String categoryId) {
        List<Product> products = new ArrayList<>();
        //Pageable pageable = PageRequest.of(page, size);
        if(categoryId.equalsIgnoreCase("all")){
            products = productRepo.globalSearchProducts(keyword, categoryId);

        }
        else{
            products = productRepo.globalSearchProductsByCategory(keyword, categoryId);
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
                            ""
                    );
                }).toList();
    }
}