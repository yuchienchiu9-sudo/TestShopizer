package com.salesmanager.test.shoppingcart;


import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BadDesignUserContextTest {

    @Test
    public void attachIpAddress_whenContextExists_setsIpAddress() {
        UserContextProvider provider = mock(UserContextProvider.class);
        when(provider.getIpAddressOrNull()).thenReturn("1.2.3.4");

        ShoppingCartUserContextV2 sut = new ShoppingCartUserContextV2(provider);
        ShoppingCart cart = new ShoppingCart();
        sut.attachIpAddress(cart);

        assertEquals("1.2.3.4", cart.getIpAddress());
        verify(provider, times(1)).getIpAddressOrNull();
    }

    @Test
    public void attachIpAddress_whenContextIsNull_doesNotSetIpAddress() {
        UserContextProvider provider = mock(UserContextProvider.class);
        when(provider.getIpAddressOrNull()).thenReturn(null);

        ShoppingCartUserContextV2 sut = new ShoppingCartUserContextV2(provider);
        ShoppingCart cart = new ShoppingCart();
        sut.attachIpAddress(cart);

        assertNull(cart.getIpAddress());
        verify(provider, times(1)).getIpAddressOrNull();
    }
}