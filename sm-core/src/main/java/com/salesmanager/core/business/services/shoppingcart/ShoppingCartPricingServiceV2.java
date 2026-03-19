package com.salesmanager.core.business.services.shoppingcart;

import com.salesmanager.core.business.services.catalog.pricing.PricingService;

import java.math.BigDecimal;

/**
 * ShoppingCartPricingServiceV2
 *
 * A testable version of the shopping cart pricing service that uses
 * Dependency Injection to accept a PricingService via its constructor,
 * instead of instantiating it directly.
 *
 * This design allows the PricingService to be mocked in unit tests,
 * making it possible to verify the shopping cart's integration with
 * pricing logic without relying on real business rules or external configs.
 */
public class ShoppingCartPricingServiceV2 {

    private final PricingService pricingService;

    /**
     * Constructor injection — makes the dependency explicit and mockable.
     *
     * @param pricingService the pricing service to use for price calculation
     */
    public ShoppingCartPricingServiceV2(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    /**
     * Computes the line total for a given unit price and quantity.
     *
     * Delegates to PricingService.calculatePriceQuantity() to perform the
     * actual calculation, respecting any business rules defined there.
     *
     * @param unitPrice the price per unit item
     * @param quantity  the number of units
     * @return total price (unitPrice * quantity, via PricingService)
     */
    public BigDecimal computeLineTotal(BigDecimal unitPrice, int quantity) {
        return pricingService.calculatePriceQuantity(unitPrice, quantity);
    }
}
