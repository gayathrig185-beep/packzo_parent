package com.ecommerce.packzo.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "category" , schema = "packzodev")
public class Category {

    @Id
    private String categoryId;
    private String categoryName;
    private String categoryDescription;
    private boolean isActive;
    /*@ManyToMany(mappedBy = "categories")
    private List<Product> products;*/
}

