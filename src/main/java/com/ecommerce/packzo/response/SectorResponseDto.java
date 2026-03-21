package com.ecommerce.packzo.response;

import java.util.List;

public record SectorResponseDto(
        Long sectorId,
        String sectorCode,
        String sectorName,
        List<CategoryDto> categories,
        int categoryPage,
        int catPageSeize,
        int totalCategories,
        int totalCategoryPages,
        boolean categoryLast
) {}
