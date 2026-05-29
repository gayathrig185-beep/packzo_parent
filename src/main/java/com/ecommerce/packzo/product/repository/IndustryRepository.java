package com.ecommerce.packzo.product.repository;

import com.ecommerce.packzo.entity.Sector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndustryRepository extends JpaRepository<Sector, Long> {

    Optional<Sector> findBySectorCodeAndIsActiveTrue(String sectorCode);

    List<Sector> findByIsActiveTrueOrderBySectorOrderAsc();
}
