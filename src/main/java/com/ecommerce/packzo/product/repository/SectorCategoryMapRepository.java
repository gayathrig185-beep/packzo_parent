package com.ecommerce.packzo.product.repository;

import com.ecommerce.packzo.entity.Category;
import com.ecommerce.packzo.entity.ProductType;
import com.ecommerce.packzo.entity.SectorCategoryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorCategoryMapRepository extends JpaRepository<SectorCategoryMap, Long> {

    @Query("""
        SELECT scm.category FROM SectorCategoryMap scm
        WHERE scm.sector.sectorId = :sectorId
        AND scm.category.isActive = true
    """)
    Page<Category> findCategoriesBySector(Long sectorId, Pageable pageable);

    @Query("""
        SELECT scm.category.categoryId FROM SectorCategoryMap scm
        WHERE scm.sector.sectorId = :sectorId
        AND scm.category.isActive = true
            """)
    List<String> findCategoriesBysector(Long sectorId);

    @Query("""
        SELECT e from  Category e where categoryId in (Select scm.category.categoryId FROM SectorCategoryMap scm
        WHERE scm.sector.sectorId = :sectorId
        AND scm.category.isActive = true)
            """)
    List<Category> findCategoriesListBysector(Long sectorId);

    @Query("""
    SELECT pt FROM CategoryProductTypeMap m
    JOIN m.productType pt
    JOIN SectorCategoryMap scm ON scm.category = m.category
    WHERE scm.sector.sectorId = :sectorId
      AND m.category.categoryId = :categoryId
      AND pt.isActive = true
""")
    Page<ProductType> findByCategoryAndSector(
            String categoryId,
            Long sectorId,
            Pageable pageable
    );
}