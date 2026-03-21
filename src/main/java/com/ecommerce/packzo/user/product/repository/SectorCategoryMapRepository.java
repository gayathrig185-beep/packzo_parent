package com.ecommerce.packzo.user.product.repository;

import com.ecommerce.packzo.entity.Category;
import com.ecommerce.packzo.entity.SectorCategoryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorCategoryMapRepository extends JpaRepository<SectorCategoryMap, Long> {

    @Query("""
        SELECT scm.category FROM SectorCategoryMap scm
        WHERE scm.sector.sectorId = :sectorId
        AND scm.category.isActive = true
    """)
    Page<Category> findCategoriesBySector(Long sectorId, Pageable pageable);
}