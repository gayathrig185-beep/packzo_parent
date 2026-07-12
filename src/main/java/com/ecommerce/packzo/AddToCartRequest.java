package com.ecommerce.packzo;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Data
public class AddToCartRequest {

    @NotBlank
    private String userId;

    @NotNull
    private String productVariantId;

    @Min(1)
    private Integer quantity;
}