package com.ecommerce.packzo.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartRequest {

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity should be greater than zero")
    private Integer quantity;

}