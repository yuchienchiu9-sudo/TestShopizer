package com.salesmanager.test.shoppingcart;

import java.math.BigDecimal;
import com.salesmanager.core.business.services.catalog.pricing.PricingService;

public class ShoppingCartPricingServiceV2 {

    private final PricingService pricingService;

    public ShoppingCartPricingServiceV2(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    // dummy but testable: cart uses PricingService to compute line total
    public BigDecimal computeLineTotal(BigDecimal unitPrice, int qty) {
        if (qty <= 0) return BigDecimal.ZERO;
        return pricingService.calculatePriceQuantity(unitPrice, qty);
    }
}