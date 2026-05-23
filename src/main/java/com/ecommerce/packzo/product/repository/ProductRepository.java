package com.ecommerce.packzo.product.repository;

import com.ecommerce.packzo.entity.Product;
import com.ecommerce.packzo.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //Page<Product> findByCategoryCategoryIdAndIsActiveTrue(String categoryId, Pageable productPage);

    Page<Product> findByProductTypeTypeId(String productTypeId, Pageable productPage);

    Product findByProductIdAndIsActiveTrue(Long productId);


    @Query("""
            Select p from Product p where  p.productType.typeId in (Select cp.productType.typeId from CategoryProductTypeMap cp  where cp.category.categoryId = :categoryId )
            and p.isActive = true
            """)
    Page<Product> findProductsByCategory(String categoryId, Pageable prdPage);


    @Query("""
            Select p from Product p where  p.productType.typeId in (Select cp.productType.typeId from CategoryProductTypeMap cp  where cp.category.categoryId = :categoryId )
            and p.isActive = true
            """)
    List<Product> findProductsByCategory(String categoryId);



    @Query("""
            SELECT DISTINCT p FROM Product p
                                      JOIN p.productType pt
                                      JOIN CategoryProductTypeMap cp ON cp.productType = pt
                                      JOIN cp.category c
                                      JOIN SectorCategoryMap scm ON scm.category = c
                                      JOIN ProductSectorMap psm ON psm.productType = pt
                                      WHERE c.categoryId = :categoryId
                                        AND scm.sector.sectorId = :sectorId
                                        AND psm.sector.sectorId = :sectorId
                                        AND p.isActive = true
            """)
    Page<Product> findProductsByCategoryAndSector(String categoryId, long sectorId, Pageable prdPage);

    @Query("""
            SELECT DISTINCT p FROM Product p
                                      JOIN p.productType pt
                                      JOIN CategoryProductTypeMap cp ON cp.productType = pt
                                      JOIN cp.category c
                                      JOIN SectorCategoryMap scm ON scm.category = c
                                      JOIN ProductSectorMap psm ON psm.productType = pt
                                      WHERE c.categoryId = :categoryId
                                        AND scm.sector.sectorId = :sectorId
                                        AND psm.sector.sectorId = :sectorId
                                        AND p.isActive = true
            """)
    List<Product> findProductsByCategoryAndSector(String categoryId, long sectorId);

    @Query("""
            SELECT p FROM Product p WHERE p.isActive = true
            AND EXISTS (
                        SELECT 1 FROM CategoryProductTypeMap cp
                        JOIN SectorCategoryMap scm ON scm.category = cp.category
                        WHERE cp.productType = p.productType AND cp.category.categoryId = :categoryId AND scm.sector.sectorId = :sectorId)
                        AND EXISTS (SELECT 1 FROM ProductSectorMap psm
                                           WHERE psm.productType = p.productType
                                             AND psm.sector.sectorId = :sectorId
                                       )AND (:productTypeName IS NULL OR LOWER(p.productType.typeName) LIKE LOWER(CONCAT('%', :productTypeName, '%')))
            
            """)
    Page<Product> findProductsByCategoryAndSectorbyProductId(String categoryId, long sectorId, String productTypeName,Pageable prdPage);


    //List<ProductVariant> findProductsByVariant(Long productId);
}
