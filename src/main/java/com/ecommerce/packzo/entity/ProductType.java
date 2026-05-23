package com.ecommerce.packzo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name= "product_type", schema = "packzodev")
public class ProductType {
    @Id
    private String typeId;
    private String typeName;
    private boolean isActive;
}
