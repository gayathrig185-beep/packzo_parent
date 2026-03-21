package com.ecommerce.packzo.user.product.controller;

import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.response.CategoryDto;
import com.ecommerce.packzo.response.SectorResponseDto;
import com.ecommerce.packzo.user.product.service.impl.CatalogServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    // Browse All
    @GetMapping("/browse-all/{categoryId}/products")
    public CategoryDto browseAllByCategory(@PathVariable String categoryId,@RequestParam String key, @RequestParam int prdPageNo ,
                                       @RequestParam int prdPageSize) {

        Optional.ofNullable(key).filter(StringUtils::hasText)
                .filter(keyValue -> keyValue.equalsIgnoreCase("all"))
                .orElseThrow(() -> new PaczoException(SERVICE_002,INVALID_KEY_DATA,INVALID_KEY_DATA));
        String categoryValue = Optional.ofNullable(categoryId).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_008,INVALID_CATEGORY_ID,INVALID_CATEGORY_ID));

        return Optional.ofNullable(catalogService.browseAllByCategory(categoryValue,prdPageNo, prdPageSize))
                .orElseThrow(() -> new PaczoException(SERVICE_003, INVALID_PRD_LIST, INVALID_PRD_LIST));
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
    public CategoryDto browseBySectorCategory(@PathVariable String categoryId, @RequestParam String sectorId, @RequestParam int prdPageNo , @RequestParam int pageSize) {
        String sectCode = Optional.ofNullable(sectorId).filter(StringUtils::hasText)
                .orElseThrow(() ->  new PaczoException(SERVICE_010,INVALID_SECTORID,INVALID_SECTORID));
        String categoryValue = Optional.ofNullable(categoryId).filter(StringUtils::hasText)
                .orElseThrow(() -> new PaczoException(SERVICE_008,INVALID_CATEGORY_ID,INVALID_CATEGORY_ID));
        return catalogService.browseByCategory(sectCode,categoryValue,prdPageNo, pageSize);
    }
}
