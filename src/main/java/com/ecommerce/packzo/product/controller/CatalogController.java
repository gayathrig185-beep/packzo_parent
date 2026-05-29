package com.ecommerce.packzo.product.controller;

import com.ecommerce.packzo.entity.Product;
import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.response.CategoryDto;
import com.ecommerce.packzo.response.CategoryListResponse;
import com.ecommerce.packzo.response.ProductDto;
import com.ecommerce.packzo.response.SectorResponseDto;
import com.ecommerce.packzo.product.service.impl.CatalogServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.ecommerce.packzo.constants.ErrorConstants.*;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private final CatalogServiceImpl catalogService;

    public CatalogController(CatalogServiceImpl catalogService) {
        this.catalogService = catalogService;
    }

    // Browse All
    @GetMapping("/browse-all")
    public List<CategoryDto> browseAll(@RequestParam String key, @RequestParam int catPageNo, @RequestParam int catPageSize, @RequestParam int prdPageNo ,
                                       @RequestParam int prdPageSize) {

        Optional.ofNullable(key).filter(StringUtils::hasText)
                .filter(keyValue -> keyValue.equalsIgnoreCase("all"))
                .orElseThrow(() -> new PaczoException(SERVICE_002,INVALID_KEY_DATA,INVALID_KEY_DATA));

        return Optional.ofNullable(catalogService.browseAll(catPageNo, catPageSize, prdPageNo, prdPageSize))
                .filter(catList -> !catList.isEmpty())
                .orElseThrow(() -> new PaczoException(SERVICE_003, INVALID_PRD_LIST, INVALID_PRD_LIST));
    }

    // see all
    //When user clicks on see all -List all the products
    @GetMapping("/see-all/{categoryId}/products")
    public List<ProductDto> seeAllProducts(@PathVariable String categoryId, @RequestParam String key) {

        Optional.ofNullable(key).filter(StringUtils::hasText)
                .filter(keyValue -> keyValue.equalsIgnoreCase("all"))
                .orElseThrow(() -> new PaczoException(SERVICE_012,INVALID_KEY_DATA,INVALID_KEY_DATA));
        String categoryValue = Optional.ofNullable(categoryId).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_008,INVALID_CATEGORY_ID,INVALID_CATEGORY_ID));

        return Optional.ofNullable(catalogService.browseAllByCategory(categoryValue))
                .orElseThrow(() -> new PaczoException(SERVICE_013, INVALID_PRD_LIST, INVALID_PRD_LIST));
    }

    @GetMapping("/browse-all/{categoryId}/productsBycategory")
    public CategoryDto getProductsByCatgeory(@PathVariable String categoryId, @RequestParam String key ,@RequestParam int prdPageNo ,
                                                  @RequestParam int prdPageSize) {
        CategoryDto categoryDto = null;
        String keyData = Optional.ofNullable(key).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_012,INVALID_KEY_DATA,INVALID_KEY_DATA));
        String categoryValue = Optional.ofNullable(categoryId).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_008,INVALID_CATEGORY_ID,INVALID_CATEGORY_ID));

        if(keyData.equalsIgnoreCase("all")){
            categoryDto = Optional.ofNullable(catalogService.browseAllByCategoryByPagination(categoryValue, prdPageNo, prdPageSize))
                    .orElseThrow(() -> new PaczoException(SERVICE_013, INVALID_PRD_LIST, INVALID_PRD_LIST));
        }
        else{

        }

        return categoryDto;
    }



    // Sector-wise
    @GetMapping("/sectors/{sectorCode}/products")
    public ResponseEntity<SectorResponseDto> browseBySector(
            @PathVariable String sectorCode, @RequestParam int catPageNo, @RequestParam int catPageSize, @RequestParam int prdPageNo ,
            @RequestParam int prdPageSize) {
        String sectCode = Optional.ofNullable(sectorCode).filter(StringUtils::hasText)
                .orElseThrow(() ->  new PaczoException(SERVICE_001,INVALID_SECTORCODE,INVALID_SECTORCODE));
        return ResponseEntity.ok(
                catalogService.browseBySector(sectCode, catPageNo, catPageSize, prdPageNo, prdPageSize));
    }

    // Category-wise
    @GetMapping("/category/{categoryId}/products")
    public List<ProductDto> browseBySectorCategory(@PathVariable String categoryId, @RequestParam String sectorId) {
        String sectCode = Optional.ofNullable(sectorId).filter(StringUtils::hasText)
                .orElseThrow(() ->  new PaczoException(SERVICE_010,INVALID_SECTORID,INVALID_SECTORID));
        String categoryValue = Optional.ofNullable(categoryId).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_008,INVALID_CATEGORY_ID,INVALID_CATEGORY_ID));
        return catalogService.browseBySectorCategory(sectCode,categoryValue);
    }

    @GetMapping("/productType/{productTypeName}/products")
    public CategoryDto browseBySectorCategoryProductTypeId(@PathVariable String productTypeName, @RequestParam String categoryId, @RequestParam String businessType, @RequestParam int prdPageNo , @RequestParam int pageSize) {
        String sectCode = Optional.ofNullable(businessType).filter(StringUtils::hasText)
                .orElseThrow(() ->  new PaczoException(SERVICE_010,INVALID_SECTORID,INVALID_SECTORID));
        String categoryValue = Optional.ofNullable(categoryId).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_008,INVALID_CATEGORY_ID,INVALID_CATEGORY_ID));
        return catalogService.browseByProductTypeId(sectCode,categoryValue,productTypeName,prdPageNo, pageSize);
    }

    @GetMapping("/productType/filterList/{key}")
    public Map<String,List<String>> filterProductTypeList(@PathVariable String key){
        Map<String,List<String>> filterMap = new HashMap<>();
        String keyValue = Optional.ofNullable(key).filter(StringUtils::hasText).orElseThrow();
        if(keyValue.equals("all")){
           filterMap =  catalogService.getFilterValueForBrowseAll();
        }else{
            filterMap = catalogService.getFilterValueBySectorCode(key);
        }
        return filterMap;

    }

    @GetMapping("/getCategories/{key}")
    public List<CategoryListResponse> getCategoryList(@PathVariable String key){
        return catalogService.getCategories();
    }




}
