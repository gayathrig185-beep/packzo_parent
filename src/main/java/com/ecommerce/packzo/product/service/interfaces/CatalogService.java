package com.ecommerce.packzo.product.service.interfaces;

import com.ecommerce.packzo.response.CategoryDto;
import com.ecommerce.packzo.response.CategoryListResponse;
import com.ecommerce.packzo.response.SectorResponseDto;
import java.util.Map;

import java.util.List;

public interface CatalogService {

     List<CategoryDto> browseAll(int catPageNo, int catPageSize, int prdPageNo , int prdPageSize);

     SectorResponseDto browseBySector(String sectorCode, int catPageNo, int catPageSize, int prdPageNo, int prdPageSize);

     CategoryDto browseByProductTypeId(String sectCode, String categoryValue, String productTypeId, int prdPageNo, int prdPageSize);

     Map<String, List<String>> getFilterValueForBrowseAll();

     Map<String, List<String>> getFilterValueBySectorCode(String key);

     List<CategoryListResponse> getCategories();


}
