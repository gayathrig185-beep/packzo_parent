package com.ecommerce.packzo.mapper;

import com.ecommerce.packzo.entity.Cart;
import com.ecommerce.packzo.entity.ProductVariant;
import com.ecommerce.packzo.response.CartItemDto;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    public CartItemDto toResponse(Cart cart) {

        ProductVariant variant = cart.getProductVariant();

        return CartItemDto.builder()
                .cartId(cart.getCartId())
                .productId(variant.getProduct().getProductId())
                .productVariantId(variant.getVariantId())
                .productName(variant.getProduct().getProductName())
                .variantName(variant.getVariantName())
                .thumbnailUrl(variant.getImageUrl())
                .quantity(cart.getQuantity())
                .outOfStock(variant.getQuantity() > 0)
                .build();
    }


}