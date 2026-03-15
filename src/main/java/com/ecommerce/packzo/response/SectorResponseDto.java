package com.ecommerce.packzo.response;

import java.util.List;

public record SectorResponseDto(
        String sectorCode,
        String sectorName,
        List<CategoryDto> categories
) {}
