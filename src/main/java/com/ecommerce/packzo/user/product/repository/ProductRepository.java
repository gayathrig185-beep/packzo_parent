package com.ecommerce.packzo.user.product.repository;

import com.ecommerce.packzo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryAndIsActiveTrue(String categoryId);

    Product findByProductIdAndIsActiveTrue(Long productId);
}
