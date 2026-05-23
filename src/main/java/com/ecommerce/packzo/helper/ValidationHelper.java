package com.ecommerce.packzo.helper;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ValidationHelper {


    public String calculationOfDiscountedPrice(BigDecimal originalPrice, BigDecimal discountedPrice) {
        BigDecimal savings = originalPrice.subtract(discountedPrice);
        BigDecimal percentageDiscount = savings
                .divide(originalPrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        return String.valueOf(percentageDiscount+"%");

    }
}
