package com.salesmanager.test.shoppingcart;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.salesmanager.core.business.services.catalog.pricing.PricingService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartPricingServiceV2;

/**
 * CartMockTest
 *
 * Unit tests for ShoppingCartPricingServiceV2 using Mockito.
 *
 * Purpose: Verify that the shopping cart's pricing logic correctly
 * delegates to PricingService.calculatePriceQuantity(), and that it
 * uses the returned value properly — WITHOUT invoking any real pricing logic.
 *
 * Key Mockito Techniques:
 * - @Mock: Creates a mock PricingService
 * - when(...).thenReturn(...): Stubs the return value
 * - verify(...): Asserts the method was called with correct arguments
 * - times(1): Asserts the method was called exactly once
 */
public class CartMockTest {

    // --- Mock Declaration ---

    @Mock
    private PricingService mockPricingService;

    // --- System Under Test ---

    private ShoppingCartPricingServiceV2 cartPricingService;

    // --- Setup ---

    @Before
    public void setUp() {
        // Initialize all @Mock annotations
        MockitoAnnotations.openMocks(this);

        // Inject the mock into the system under test via constructor
        cartPricingService = new ShoppingCartPricingServiceV2(mockPricingService);
    }

    // =====================================================================
    // Test 1: Basic delegation and return value
    // =====================================================================

    /**
     * Verifies that computeLineTotal() delegates to the PricingService and
     * returns the value provided by the mock.
     *
     * Setup:
     * - unitPrice = 50.00, quantity = 2
     * - mockPricingService.calculatePriceQuantity(50.00, 2) → returns 100.00
     *
     * Expected:
     * - computeLineTotal(50.00, 2) returns 100.00
     * - calculatePriceQuantity() was called exactly once with (50.00, 2)
     */
    @Test
    public void testTotalCalculationWithMockedPrice() {
        // Arrange
        BigDecimal unitPrice = new BigDecimal("50.00");
        int quantity = 2;
        BigDecimal expectedTotal = new BigDecimal("100.00");

        when(mockPricingService.calculatePriceQuantity(unitPrice, quantity))
                .thenReturn(expectedTotal);

        // Act
        BigDecimal actualTotal = cartPricingService.computeLineTotal(unitPrice, quantity);

        // Assert — return value is correct
        assertEquals("Line total should be unit price × quantity", expectedTotal, actualTotal);

        // Verify — PricingService was called exactly once with the right arguments
        verify(mockPricingService, times(1))
                .calculatePriceQuantity(unitPrice, quantity);
    }

    // =====================================================================
    // Test 2: Single item quantity
    // =====================================================================

    /**
     * Verifies correct behavior when quantity = 1.
     *
     * Setup:
     * - unitPrice = 29.99, quantity = 1
     * - mock returns 29.99
     *
     * Expected:
     * - computeLineTotal returns 29.99
     * - calculatePriceQuantity called once with (29.99, 1)
     */
    @Test
    public void testTotalCalculationWithSingleQuantity() {
        BigDecimal unitPrice = new BigDecimal("29.99");
        int quantity = 1;
        BigDecimal expectedTotal = new BigDecimal("29.99");

        when(mockPricingService.calculatePriceQuantity(unitPrice, quantity))
                .thenReturn(expectedTotal);

        BigDecimal actualTotal = cartPricingService.computeLineTotal(unitPrice, quantity);

        assertEquals(expectedTotal, actualTotal);
        verify(mockPricingService, times(1)).calculatePriceQuantity(unitPrice, quantity);
    }

    // =====================================================================
    // Test 3: No interaction with real PricingService
    // =====================================================================

    /**
     * Confirms that only calculatePriceQuantity() is called, and no other
     * PricingService methods are invoked by computeLineTotal().
     *
     * This ensures the V2 service has minimal coupling to the PricingService.
     */
    @Test
    public void testNoUnexpectedInteractionsWithPricingService() {
        BigDecimal unitPrice = new BigDecimal("15.00");
        int quantity = 3;

        when(mockPricingService.calculatePriceQuantity(unitPrice, quantity))
                .thenReturn(new BigDecimal("45.00"));

        cartPricingService.computeLineTotal(unitPrice, quantity);

        // Verify only one specific method was called, nothing else
        verify(mockPricingService).calculatePriceQuantity(unitPrice, quantity);
        verifyNoMoreInteractions(mockPricingService);
    }
}
