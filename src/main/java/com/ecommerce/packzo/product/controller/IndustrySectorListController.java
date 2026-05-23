package com.ecommerce.packzo.product.controller;

import com.ecommerce.packzo.response.SectorDto;
import com.ecommerce.packzo.product.service.interfaces.IndustryListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sectors")
public class IndustrySectorListController {

    private static final Logger logger = LoggerFactory.getLogger(IndustrySectorListController.class);

    private final IndustryListService sectorService;

    public IndustrySectorListController(IndustryListService sectorService) {
        this.sectorService = sectorService;
    }

    // ⭐ Get all active sectors
    @GetMapping
    public List<SectorDto> getSectors() {
        logger.debug("IndustrySectorListController Method Starts");
        return sectorService.getAllActiveSectors();
    }
}