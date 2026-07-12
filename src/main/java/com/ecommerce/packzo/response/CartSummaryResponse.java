package com.ecommerce.packzo.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartSummaryResponse {

    private Integer totalItems;

    private Integer totalUniqueProducts;

}