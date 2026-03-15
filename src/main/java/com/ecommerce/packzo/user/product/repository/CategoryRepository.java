package com.ecommerce.packzo.user.product.repository;

import com.ecommerce.packzo.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<ProductCategory, String> {

    List<ProductCategory> findByIsActiveTrue();

    @Query(value = "SELECT pc FROM ProductCategory pc WHERE pc.sector.sectorId = :sectorId and isActive = true")
    List<ProductCategory> findBySectorSectorId(@Param("sectorId")Long sectorId);
}
