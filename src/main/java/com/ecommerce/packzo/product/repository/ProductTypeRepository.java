package com.ecommerce.packzo.product.repository;

import com.ecommerce.packzo.entity.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductTypeRepository extends JpaRepository<ProductType, String> {

    @Query("""
        SELECT pt FROM CategoryProductTypeMap m
        JOIN m.productType pt
        WHERE m.category.categoryId = :categoryId
          AND pt.isActive = true
    """)
    List<ProductType> findByCategoryId(String categoryId);

    Page<ProductType> findByIsActiveTrue(Pageable pageable);
}