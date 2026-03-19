package com.salesmanager.test.shoppingcart;


import com.salesmanager.core.model.shoppingcart.ShoppingCart;

public class ShoppingCartUserContextV2 {
    private final UserContextProvider provider;

    public ShoppingCartUserContextV2(UserContextProvider provider) {
        this.provider = provider;
    }

    public void attachIpAddress(ShoppingCart cart) {
        String ip = provider.getIpAddressOrNull();
        if (ip != null) cart.setIpAddress(ip);
    }
}