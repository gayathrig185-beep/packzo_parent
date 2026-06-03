package com.ecommerce.packzo.product.controller;

import com.ecommerce.packzo.entity.Product;
import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.response.CategoryDto;
import com.ecommerce.packzo.response.CategoryListResponse;
import com.ecommerce.packzo.response.ProductDto;
import com.ecommerce.packzo.product.service.impl.CatalogServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public List<CategoryDto> browseByBusinessType(@RequestParam String key, @RequestParam int catPageNo, @RequestParam int catPageSize, @RequestParam int prdPageNo,
                                                  @RequestParam int prdPageSize) {
        logger.debug("CatalogController:::-> method begins");
        logger.debug("Params:: key {}, catPageNo {} , catPageSize {} , prdPageNo {}, prdPageSize {}",key, catPageNo, catPageSize, prdPageNo, prdPageSize);
        List<CategoryDto> categoryList;

        List<String> businessTypeList = Arrays.asList("CORP", "CATT", "CLD", "WED", "REST", "HOTEL", "BIO", "BLK", "HOSP", "DECOR");

        key = Optional.ofNullable(key).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_002, INVALID_KEY_DATA, INVALID_KEY_DATA));

        if (key.equalsIgnoreCase("all")) {
            categoryList = Optional.ofNullable(catalogService.browseAll(catPageNo, catPageSize, prdPageNo, prdPageSize))
                    .filter(catList -> !catList.isEmpty())
                    .orElseThrow(() -> new PaczoException(SERVICE_003, INVALID_PRD_LIST, INVALID_PRD_LIST));
        } else {
            if (businessTypeList.contains(key)) {
                categoryList = Optional.ofNullable(catalogService.browseBySector(key, catPageNo, catPageSize, prdPageNo, prdPageSize))
                        .filter(catList -> !catList.isEmpty()).orElseThrow(() -> new PaczoException(SERVICE_003, INVALID_PRD_LIST, INVALID_PRD_LIST));
            } else {
               throw new PaczoException(SERVICE_015, INVALID_SECTORID, INVALID_SECTORID);
            }
        }
        logger.debug("CategoryList:::{}",categoryList);
        logger.debug("CatalogController:::-> method ends");
        return categoryList;
    }

    // see all
    //When user clicks on see all -List all the products
    @GetMapping("/see-all/{categoryId}/products")
    public List<ProductDto> seeAllProducts(@PathVariable String categoryId, @RequestParam String key) {
        List<String> businessTypeList = Arrays.asList("CORP", "CATT", "CLD", "WED", "REST", "HOTEL", "BIO", "BLK", "HOSP", "DECOR");
        List<ProductDto> productDtoList = new ArrayList<>();
        
        key = Optional.ofNullable(key).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_012, INVALID_KEY_DATA, INVALID_KEY_DATA));
        String categoryValue = Optional.ofNullable(categoryId).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_008, INVALID_CATEGORY_ID, INVALID_CATEGORY_ID));
        if(key.equalsIgnoreCase("all")){
            productDtoList = Optional.ofNullable(catalogService.browseAllByCategory(categoryValue))
                    .orElseThrow(() -> new PaczoException(SERVICE_013, INVALID_PRD_LIST, INVALID_PRD_LIST));
        }else{
            if (businessTypeList.contains(key)) {
                productDtoList = Optional.ofNullable(catalogService.browseBySectorCategory(key, categoryValue)).filter(catList -> !catList.isEmpty())
                        .orElseThrow(() -> new PaczoException(SERVICE_024, INVALID_PRD_LIST, INVALID_PRD_LIST));
            } else {
                throw new PaczoException(SERVICE_015, INVALID_SECTORID, INVALID_SECTORID);
            }
        }
        return productDtoList;
    }

    //seeALL
    @GetMapping("/browse-all/{categoryId}/productsBycategory")
    public CategoryDto getProductsByCatgeory(@PathVariable String categoryId, @RequestParam String key, @RequestParam int prdPageNo,
                                             @RequestParam int prdPageSize) {
        CategoryDto categoryDto = null;
        String keyData = Optional.ofNullable(key).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_012, INVALID_KEY_DATA, INVALID_KEY_DATA));
        String categoryValue = Optional.ofNullable(categoryId).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_008, INVALID_CATEGORY_ID, INVALID_CATEGORY_ID));

        List<String> businessTypeList = Arrays.asList("CORP", "CATT", "CLD", "WED", "REST", "HOTEL", "BIO", "BLK", "HOSP", "DECOR");

        if (keyData.equalsIgnoreCase("all")) {
            categoryDto = Optional.ofNullable(catalogService.browseAllByCategoryByPagination(categoryValue, prdPageNo, prdPageSize))
                    .orElseThrow(() -> new PaczoException(SERVICE_016, INVALID_PRD_LIST, INVALID_PRD_LIST));
        } else {
            if (businessTypeList.contains(key)) {
                categoryDto = Optional.ofNullable(catalogService.browseBySectorByPagination(keyData, categoryValue, prdPageNo, prdPageSize))
                        .orElseThrow(() -> new PaczoException(SERVICE_017, INVALID_PRD_LIST, INVALID_PRD_LIST));
            }else{
                throw new PaczoException(SERVICE_018, INVALID_SECTORID, INVALID_SECTORID);
            }
        }
        return categoryDto;
    }


    @GetMapping("/productType/{productTypeName}/products")
    public CategoryDto filterByProductTypeId(@PathVariable String productTypeName, @RequestParam String categoryId, @RequestParam String businessType, @RequestParam int prdPageNo, @RequestParam int pageSize) {
        String sectCode = Optional.ofNullable(businessType).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_010, INVALID_SECTORID, INVALID_SECTORID));
        String categoryValue = Optional.ofNullable(categoryId).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_008, INVALID_CATEGORY_ID, INVALID_CATEGORY_ID));
        return catalogService.browseByProductTypeId(sectCode, categoryValue, productTypeName, prdPageNo, pageSize);
    }


    @GetMapping("/getCategories/{key}")
    public List<CategoryListResponse> getCategoryList(@PathVariable String key) {
        return catalogService.getCategories();
    }


}
