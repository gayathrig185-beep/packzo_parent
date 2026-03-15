package com.ecommerce.packzo.user.product.service.interfaces;

import com.ecommerce.packzo.response.CategoryDto;
import com.ecommerce.packzo.response.SectorResponseDto;

import java.util.List;

public interface CatalogService {

     List<CategoryDto> browseAll();

     SectorResponseDto browseBySector(String sectorCode);

     CategoryDto browseByCategory(String categoryId);


}
