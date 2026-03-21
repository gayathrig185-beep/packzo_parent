package com.ecommerce.packzo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="sector_category_map", schema="packzodev")
public class SectorCategoryMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @ManyToOne
    @JoinColumn(name= "category_id")
    private Category category;
}
