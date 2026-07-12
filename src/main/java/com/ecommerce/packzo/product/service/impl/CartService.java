package com.ecommerce.packzo.product.service.impl;

import com.ecommerce.packzo.PackzoApplication;
import com.ecommerce.packzo.entity.Cart;
import com.ecommerce.packzo.entity.Product;
import com.ecommerce.packzo.entity.ProductVariant;
import com.ecommerce.packzo.exception.PaczoException;
import com.ecommerce.packzo.login.constants.CartStatus;
import com.ecommerce.packzo.login.respository.GuestRespository;
import com.ecommerce.packzo.login.security.SecurityContextUtil;
import com.ecommerce.packzo.login.service.LoginService;
import com.ecommerce.packzo.mapper.CartMapper;
import com.ecommerce.packzo.product.repository.CartRepository;
import com.ecommerce.packzo.product.repository.ProductVariantRepository;
import com.ecommerce.packzo.product.service.interfaces.SecurityService;
import com.ecommerce.packzo.request.AddCartRequest;
import com.ecommerce.packzo.request.CurrentUser;
import com.ecommerce.packzo.request.UpdateCartRequest;
import com.ecommerce.packzo.request.UserContext;
import com.ecommerce.packzo.response.CartItemDto;
import com.ecommerce.packzo.response.CartResponseDto;
import com.ecommerce.packzo.response.CartSummaryResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.packzo.constants.ErrorConstants.INTERNAL_SERVER_ERROR;
import static com.ecommerce.packzo.constants.ErrorConstants.SERVICE_500;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);


    private final ProductVariantRepository variantRepository;
    private final CartRepository cartRepository;
    private final UserContextResolver userContextResolver;
    private final SecurityContextUtil securityContextUtil;
    private final GuestRespository guestRepository;
    private final SecurityService securityService;
    private final CartMapper cartMapper;


    public CartService(ProductVariantRepository variantRepository,
                       CartRepository cartRepository, UserContextResolver userContextResolver,
                       SecurityContextUtil securityContextUtil, GuestRespository guestRepository,
                       SecurityService securityService, CartMapper cartMapper) {
        this.variantRepository = variantRepository;
        this.cartRepository = cartRepository;
        this.userContextResolver = userContextResolver;
        this.securityContextUtil = securityContextUtil;
        this.guestRepository = guestRepository;
        this.securityService = securityService;
        this.cartMapper = cartMapper;
    }

    @Transactional
    public String addToCart(AddCartRequest request){

            String message = "";
            CurrentUser currentUser = securityService.getCurrentUser();

            ProductVariant variant = variantRepository.findByVariantId(request.getProductVariantId())
                            .orElseThrow(() ->
                                    new PaczoException("","",
                                            "Product variant not found"));

            Optional<Cart> existingCart;

            if (currentUser.isGuest()) {

                //loginService.isValidGuestToken(currentUser.getGuestToken());

                existingCart = cartRepository.findByGuestTokenAndProductVariant_VariantIdAndStatus(currentUser.getGuestToken(), variant.getVariantId(), CartStatus.ACTIVE);

            } else {

                existingCart = cartRepository.findByUserIdAndProductVariant_VariantIdAndStatus(currentUser.getUserId(), variant.getVariantId(), CartStatus.ACTIVE);
            }

            if (existingCart.isPresent()) {

                Cart cart = existingCart.get();

                cart.setQuantity(
                        cart.getQuantity() + request.getQuantity());

                Cart cartResponse = cartRepository.save(cart);
                if(cartResponse!=null){
                    message = "Updated Successfully";
                }
            }

            Cart cart = new Cart();

            cart.setProductVariant(variant);

            cart.setQuantity(request.getQuantity());

            cart.setStatus(CartStatus.ACTIVE);

            if (currentUser.isGuest()) {

                cart.setGuestToken(currentUser.getGuestToken());

            } else {

                cart.setUserId(currentUser.getUserId());
            }

            Cart cartResponse = cartRepository.save(cart);

            if(cartResponse!=null){
                message = "Cart added successfully";
            }
        return message;
        }


    @Transactional(readOnly = true)
    public CartResponseDto  getCart(){

        CurrentUser currentUser = securityService.getCurrentUser();

        List<Cart> carts;

        if (currentUser.isGuest()) {

            carts = cartRepository.findByGuestTokenAndStatus(currentUser.getGuestToken(), CartStatus.ACTIVE);

        } else {

            carts = cartRepository.findByUserIdAndStatus(currentUser.getUserId(), CartStatus.ACTIVE);
        }

        List<CartItemDto> items = carts.stream().map(cartMapper::toResponse)
                        .toList();

        return CartResponseDto.builder()
                .items(items)
                .guestUser(currentUser.isGuest())
                .totalItems(carts.stream()
                                .mapToInt(Cart::getQuantity)
                                .sum())
                .build();
    }

    private CartItemDto convert(Cart cart){

        ProductVariant variant= cart.getProductVariant();

        Product product= variant.getProduct();

        return CartItemDto.builder()

                .cartId(cart.getCartId())

                .productId(product.getProductId())

                .productName(product.getProductName())

                .productVariantId(variant.getVariantId())

                .capacity(variant.getCapacity())

                .noOfPieces(variant.getNoOfPieces())

                .quantity(cart.getQuantity())

                .imageUrl(product.getImageUrl())

                .outOfStock(
                        variant.getQuantity()==0)

                .build();
    }


    @Transactional
    public void mergeGuestCart(String guestToken, String userId) {

        logger.info("Merging guest cart {} into user {}", guestToken, userId);

        List<Cart> guestCartItems = cartRepository.findByGuestTokenAndStatus(guestToken, CartStatus.ACTIVE);

        if (guestCartItems.isEmpty()) {

            logger.info("Guest cart empty");

           // loginService.deactivateGuestSession(guestToken);

            return;
        }

        for (Cart guestItem : guestCartItems) {

            mergeCartItem(guestItem, userId);
        }

        cartRepository.deleteByGuestToken(guestToken);

        //loginService.deactivateGuestSession(guestToken);

        logger.info("Guest cart merged successfully");

    }

    private void mergeCartItem(Cart guestItem, String userId) {

        Optional<Cart> existingCart = cartRepository.findByUserIdAndProductVariant_VariantIdAndStatus(userId, guestItem.getProductVariant().getVariantId(), CartStatus.ACTIVE);

        if (existingCart.isPresent()) {

            Cart userCart = existingCart.get();

            userCart.setQuantity(userCart.getQuantity() + guestItem.getQuantity());

            cartRepository.save(userCart);

            return;
        }

        guestItem.setUserId(userId);

        guestItem.setGuestToken(null);

        cartRepository.save(guestItem);

    }

    public void updateQuantity(Long cartId, UpdateCartRequest request) {

        Cart cart = getOwnedCart(cartId);

        validateQuantity(request.getQuantity());

        cart.setQuantity(request.getQuantity());

        cartRepository.save(cart);
    }


    public void removeItem(Long cartId) {

        Cart cart = getOwnedCart(cartId);

        cart.setStatus(CartStatus.REMOVED);

        cartRepository.save(cart);
    }

    public void clearCart() {

        CurrentUser currentUser = securityService.getCurrentUser();

        if (currentUser.isGuest()) {

            cartRepository.deleteByGuestToken(currentUser.getGuestToken());

        } else {

            cartRepository.deleteByUserId(currentUser.getUserId());
        }
    }

    @Transactional(readOnly = true)
    public CartSummaryResponse getCartSummary() {

        CurrentUser currentUser = securityService.getCurrentUser();

        List<Cart> carts;

        if (currentUser.isGuest()) {

            carts = cartRepository.findByGuestTokenAndStatus(currentUser.getGuestToken(), CartStatus.ACTIVE);

        } else {

            carts = cartRepository.findByUserIdAndStatus(currentUser.getUserId(), CartStatus.ACTIVE);
        }

        return CartSummaryResponse.builder()
                .totalItems(
                        carts.stream()
                                .mapToInt(Cart::getQuantity)
                                .sum())
                .totalUniqueProducts(carts.size())
                .build();
    }

    private void validateQuantity(Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new PaczoException("","","Invalid quantity");
        }
    }


    private Cart getOwnedCart(Long cartId) {

        CurrentUser currentUser = securityService.getCurrentUser();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() ->
                        new PaczoException("","","Cart not found"));

        if (currentUser.isGuest()) {

            if (!currentUser.getGuestToken().equals(cart.getGuestToken())) {
                throw new PaczoException("","","Cart does not belong to current guest");
            }

        } else {

            if (!currentUser.getUserId().equals(cart.getUserId())) {
                throw new PaczoException("","","Cart does not belong to current user");
            }
        }

        return cart;
    }

}
