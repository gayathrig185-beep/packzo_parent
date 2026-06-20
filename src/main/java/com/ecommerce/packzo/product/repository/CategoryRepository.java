package com.ecommerce.packzo.product.repository;

import com.ecommerce.packzo.entity.Category;
import com.ecommerce.packzo.entity.Product;
import com.ecommerce.packzo.response.CategorySuggestionProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Page<Category> findByIsActiveTrue(Pageable categoryPage);

    @Query(value = "SELECT cat.categoryId from Category cat")
    List<String> findAllCategories();

    @Query(value = "SELECT  cat from Category cat")
    List<Category> findAllCategoriesList();

    Category findByCategoryId(String category);

/*
    @Query("""
            SELECT
                c.categoryId as categoryId,
                c.categoryName as categoryName,
                COUNT(DISTINCT p.productId) as matchedProducts,
            FROM Product p
            JOIN p.productType pt
            JOIN CategoryProductTypeMap cp
                 ON cp.productType = pt
            JOIN cp.category c
            WHERE c.categoryId <> :selectedCategoryId
            AND p.isActive = true
            AND (
                   LOWER(p.productName)
                   LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(pt.typeName)
                   LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(c.categoryName)
                   LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            GROUP BY c.categoryId, c.categoryName
            ORDER BY COUNT(DISTINCT p.productId) DESC
            """)
    List<CategorySuggestionProjection> findMatchingCategories(
            @Param("keyword") String keyword,
            @Param("selectedCategoryId") String selectedCategoryId);*/



    /*@Query(value = "SELECT pc FROM Category pc WHERE pc.sector.sectorId = :sectorId and isActive = true")
    Page<Category> findByCategoriesBySector(@Param("sectorId")Long sectorId ,Pageable categoryPage);*/
}
