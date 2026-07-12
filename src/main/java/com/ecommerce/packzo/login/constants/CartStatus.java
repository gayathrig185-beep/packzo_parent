package com.ecommerce.packzo.login.constants;

public enum CartStatus {

    /**
     * Item is currently in the cart.
     */
    ACTIVE,

    /**
     * Item has been moved to an order after successful checkout.
     */
    ORDERED,

    /**
     * Item was removed by the customer.
     */
    REMOVED,

    /**
     * Guest cart item has been merged into a user cart.
     */
    MERGED,

    /**
     * Product is no longer available (deleted/discontinued).
     */
    UNAVAILABLE

}