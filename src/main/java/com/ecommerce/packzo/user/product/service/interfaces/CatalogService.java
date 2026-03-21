package com.ecommerce.packzo.user.product.service.interfaces;

import com.ecommerce.packzo.response.CategoryDto;
import com.ecommerce.packzo.response.SectorResponseDto;

import java.util.List;

public interface CatalogService {

     List<CategoryDto> browseAll(int catPageNo, int catPageSize, int prdPageNo , int prdPageSize);

     SectorResponseDto browseBySector(String sectorCode, int catPageNo, int catPageSize, int prdPageNo, int prdPageSize);

     //CategoryDto browseByCategory(String categoryId);


}
