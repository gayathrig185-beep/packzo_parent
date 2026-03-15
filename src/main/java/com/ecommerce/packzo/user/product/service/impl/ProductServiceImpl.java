package com.ecommerce.packzo.user.product.service.impl;

import com.ecommerce.packzo.entity.Product;
import com.ecommerce.packzo.response.ProductDetailDto;
import com.ecommerce.packzo.response.VariantDto;
import com.ecommerce.packzo.user.product.repository.ProductRepository;
import com.ecommerce.packzo.user.product.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {


        private final ProductRepository productRepository;

        public ProductServiceImpl(ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        public ProductDetailDto getProductDetail(Long productId) {

            Product product = Optional.ofNullable(productRepository
                    .findByProductIdAndIsActiveTrue(productId)).filter(prd -> !prd.equals(""))
                    .orElseThrow(() ->
                            new RuntimeException("Product not found"));

            List<VariantDto> variantDtos =
                    product.getVariants()
                            .stream()
                            .map(v -> new VariantDto(
                                    v.getVariantId(),
                                    v.getModelName(),
                                    v.getAdditionalPrice()
                            ))
                            .toList();

            return new ProductDetailDto(
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductShortDescription(),
                    product.getProductLongDescription(),
                    product.getOriginalPrice(),
                    product.getDiscountPrice(),
                    product.getRating(),
                    product.getTotalRatings(),
                    product.getMaterial(),
                    product.getFeature(),
                    product.getColor(),
                    product.getCapacity(),
                    product.getAboutItem(),
                    product.getCategory().getPrdCategoryName(),
                    product.getCategory().getSector().getSectorName(),
                    variantDtos
            );
        }
}

