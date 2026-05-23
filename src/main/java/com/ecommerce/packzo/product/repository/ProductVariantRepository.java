package com.ecommerce.packzo.product.repository;

import com.ecommerce.packzo.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    //List<ProductVariant> findByProduct_ProductIdAndIsActiveTrue(Long productId);
}