package com.ecommerce.packzo.response;

import java.util.List;

public record CategoryDto(
        String id,
        String name,
        List<String> subList,
        List<ProductDto> products,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {}
