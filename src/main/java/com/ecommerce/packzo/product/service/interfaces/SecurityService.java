package com.ecommerce.packzo.product.service.interfaces;

import com.ecommerce.packzo.request.CurrentUser;

public interface SecurityService {

    CurrentUser getCurrentUser();
}