package com.ecommerce.packzo.product.repository;

import com.ecommerce.packzo.entity.Cart;
import com.ecommerce.packzo.login.constants.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    Optional<Cart> findByGuestTokenAndProductVariant_VariantId(String guestToken, String productVariantId);

    Optional<Cart> findByUserIdAndProductVariant_VariantId(String userId, String productVariantId);

    List<Cart> findByGuestToken(String guestToken);

    List<Cart> findByUserId(String userId);

    List<Cart> findByGuestTokenAndStatus(String guestToken, CartStatus status);

    Optional<Cart> findByGuestTokenAndProductVariant_VariantIdAndStatus(String guestToken, String productVariantId, CartStatus status);


    List<Cart> findByUserIdAndStatus(String userId, CartStatus status);

    Optional<Cart> findByUserIdAndProductVariant_VariantIdAndStatus(String userId, String variantId, CartStatus status);

    void deleteByGuestToken(String guestToken);

    /**
     * Delete user cart
     */
    @Modifying
    void deleteByUserId(String userId);
/*
    @Query("""
       SELECT c
       FROM Cart c
       WHERE c.userId = :userId
       AND c.status='ACTIVE'
       ORDER BY c.createdOn DESC
       """)
    List<Cart> getActiveUserCart(
            @Param("userId") String userId);*/


/*
    @Query("""
       SELECT c
       FROM Cart c
       WHERE c.guestToken=:guestToken
       AND c.status='ACTIVE'
       ORDER BY c.createdOn DESC
       """)
    List<Cart> getGuestCart(
            @Param("guestToken") String guestToken);*/


/*
    @Query("""
       SELECT SUM(c.quantity)
       FROM Cart c
       WHERE c.userId=:userId
       AND c.status='ACTIVE'
       """)
    Integer getCartQuantity(@Param("userId") String userId);*/


    @Modifying
    @Query("""
       UPDATE Cart c
       SET c.status='REMOVED'
       WHERE c.cartId=:cartId
       """)
    int removeCartItem(
            @Param("cartId") Long cartId);



}