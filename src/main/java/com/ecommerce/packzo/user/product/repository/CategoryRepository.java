package com.ecommerce.packzo.user.product.repository;

import com.ecommerce.packzo.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<ProductCategory, String> {

    List<ProductCategory> findByIsActiveTrue();

    List<ProductCategory> findBySectorAndIsActiveTrue(String code);
}
