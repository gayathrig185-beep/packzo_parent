package com.ecommerce.packzo.product.repository;

import com.ecommerce.packzo.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Query("""
       select v
       from ProductVariant v
       where v.product.productId = :productId
       order by v.capacity asc,
                v.noOfPieces asc
       """)
    List<ProductVariant> findVariants(Long productId);

    List<ProductVariant> findProductsByVariantId(Long productId);
}