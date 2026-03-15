package com.ecommerce.packzo.user.product.controller;

import com.ecommerce.packzo.exception.PackzoException;
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
    public List<CategoryDto> browseAll(@RequestParam String key) {

        Optional.ofNullable(key).filter(StringUtils::hasText)
                .filter(keyValue -> keyValue.equalsIgnoreCase("all"))
                .orElseThrow(() -> new PackzoException(SERVICE_002,INVALID_KEY_DATA,INVALID_KEY_DATA));

        return Optional.ofNullable(catalogService.browseAll())
                .filter(prodList -> !prodList.isEmpty())
                .orElseThrow(() -> new PackzoException(SERVICE_003, INVALID_PRD_LIST, INVALID_PRD_LIST));
    }

    // Sector-wise
    @GetMapping("/sectors/{sectorCode}")
    public ResponseEntity<SectorResponseDto> browseBySector(
            @PathVariable String sectorCode) {
        String sectCode = Optional.ofNullable(sectorCode).filter(StringUtils::hasText)
                .orElseThrow(() ->  new PackzoException(SERVICE_001,INVALID_SECTORCODE,INVALID_SECTORCODE));
        return ResponseEntity.ok(
                catalogService.browseBySector(sectCode));
    }

    // Category-wise
    @GetMapping("/categories/{categoryId}")
    public CategoryDto browseByCategory(
            @PathVariable String categoryId) {
        String categoryValue = Optional.ofNullable(categoryId).filter(StringUtils::hasText)
                .orElseThrow(() -> new PackzoException(SERVICE_008,INVALID_CATEGORY_ID,INVALID_CATEGORY_ID));
        return catalogService.browseByCategory(categoryValue);
    }
}
