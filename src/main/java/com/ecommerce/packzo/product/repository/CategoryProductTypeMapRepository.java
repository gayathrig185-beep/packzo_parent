package com.ecommerce.packzo.product.repository;

import com.ecommerce.packzo.entity.CategoryProductTypeMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryProductTypeMapRepository
        extends JpaRepository<CategoryProductTypeMap, Long> {

    // ✅ Get all mappings for a category
    List<CategoryProductTypeMap> findByCategory_CategoryId(String categoryId);

    // ✅ Get all mappings for a product type
    List<CategoryProductTypeMap> findByProductType_TypeId(String typeId);

    // ✅ Check if mapping exists (avoid duplicates)
    boolean existsByCategory_CategoryIdAndProductType_TypeId(
            String categoryId,
            String typeId
    );
}