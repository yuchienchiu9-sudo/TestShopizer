package com.salesmanager.test.shoppingcart;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.Mockito;
import java.math.BigDecimal;
import com.salesmanager.core.business.services.catalog.pricing.PricingService;

public class CartPricingMockTest {

    @Test
    public void testTotalCalculationWithMockedPrice() {
        PricingService mockPricing = Mockito.mock(PricingService.class);
        ShoppingCartPricingServiceV2 sut = new ShoppingCartPricingServiceV2(mockPricing);

        BigDecimal unitPrice = new BigDecimal("50.0");
        int qty = 1;

        when(mockPricing.calculatePriceQuantity(eq(unitPrice), eq(qty)))
                .thenReturn(new BigDecimal("100.00"));

        BigDecimal result = sut.computeLineTotal(unitPrice, qty);

        assertEquals(new BigDecimal("100.00"), result);

        verify(mockPricing, times(1)).calculatePriceQuantity(eq(unitPrice), eq(qty));
    }
    @Test
    public void computeLineTotal_whenQtyIsZero_returnsZero_andDoesNotCallPricingService() {
        PricingService mockPricing = Mockito.mock(PricingService.class);

        ShoppingCartPricingServiceV2 sut = new ShoppingCartPricingServiceV2(mockPricing);

        BigDecimal result = sut.computeLineTotal(new BigDecimal("50.0"), 0);

        assertEquals(BigDecimal.ZERO, result);

        // Key point: no interaction with dependency when qty <= 0
        verifyNoInteractions(mockPricing);
    }

}