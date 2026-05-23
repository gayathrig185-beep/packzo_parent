package com.ecommerce.packzo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantGroupDto {

    private String capacity;              // 90ML, 120ML

    private List<OptionDto> options;      // pieces under this capacity
}