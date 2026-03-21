package com.ecommerce.packzo.user.product.repository;

import com.ecommerce.packzo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryCategoryIdAndIsActiveTrue(String categoryId, Pageable productPage);

    Product findByProductIdAndIsActiveTrue(Long productId);

    @Query("""
            Select p from Product p join p.category c join SectorCategoryMap  s on s.category = c where c.categoryId = :categoryId
            and s.sector.sectorId = :sectorId
            and p.isActive = true
            """)
    Optional<Page<Product>> findByCategoryAndSector(String categoryId, String sectorId, Pageable prdPage);
}
