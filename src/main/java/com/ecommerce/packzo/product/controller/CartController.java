package com.ecommerce.packzo.product.controller;

import com.ecommerce.packzo.product.service.impl.CartService;
import com.ecommerce.packzo.request.AddCartRequest;
import com.ecommerce.packzo.request.UpdateCartRequest;
import com.ecommerce.packzo.response.CartResponseDto;
import com.ecommerce.packzo.response.CartSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/addToCart")
    public ResponseEntity<String> addToCart(@Valid @RequestBody AddCartRequest request){

        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @GetMapping("/viewCart")
    public ResponseEntity<CartResponseDto> getCart(){

        return ResponseEntity.ok(cartService.getCart());
    }

    @PutMapping("/updateCart/{cartId}")
    public ResponseEntity<String> updateCart(@PathVariable Long cartId, @Valid @RequestBody UpdateCartRequest request) {

        cartService.updateQuantity(cartId, request);

        return ResponseEntity.ok("Cart updated successfully");
    }

    @DeleteMapping("/remove/{cartId}")
    public ResponseEntity<String> removeItem(@PathVariable Long cartId) {

        cartService.removeItem(cartId);

        return ResponseEntity.ok("Item removed successfully");
    }

    @DeleteMapping("/clearCart")
    public ResponseEntity<String> clearCart() {

        cartService.clearCart();

        return ResponseEntity.ok("Cart cleared successfully");
    }

    @GetMapping("/summary")
    public ResponseEntity<CartSummaryResponse> getSummary() {

        CartSummaryResponse response = cartService.getCartSummary();

        return ResponseEntity.ok(response);
    }
}