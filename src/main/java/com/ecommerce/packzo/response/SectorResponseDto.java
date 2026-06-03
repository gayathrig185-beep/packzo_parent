package com.ecommerce.packzo.response;

import java.util.List;

public record SectorResponseDto(
        Long sectorId,
        String sectorCode,
        String sectorName,
        CategoryResponseDto categories
) {}
