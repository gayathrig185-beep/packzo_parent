package com.ecommerce.packzo.response;

import java.util.List;

public record CategoryDto(
        String id,
        String name,
        List<String> subList,
        ProductPageDto productPageDto
) {}
