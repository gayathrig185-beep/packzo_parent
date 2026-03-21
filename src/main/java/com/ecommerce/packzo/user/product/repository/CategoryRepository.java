package com.ecommerce.packzo.user.product.repository;

import com.ecommerce.packzo.entity.Category;
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

    /*@Query(value = "SELECT pc FROM Category pc WHERE pc.sector.sectorId = :sectorId and isActive = true")
    Page<Category> findByCategoriesBySector(@Param("sectorId")Long sectorId ,Pageable categoryPage);*/
}
