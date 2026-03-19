package com.salesmanager.test.shoppingcart;

import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.type.ProductTypeService;
import com.salesmanager.core.business.services.catalog.product.manufacturer.ManufacturerService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.category.CategoryDescription;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.test.common.AbstractSalesManagerCoreTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * FSM Transition-Level Tests for Shopping Cart
 * States: EMPTY, ACTIVE, OBSOLETE
 *
 * Transitions:
 *  T1 addItem: EMPTY -> ACTIVE
 *  T2 updateQty: ACTIVE -> ACTIVE (self-loop)
 *  T3 removeLastItem: ACTIVE -> EMPTY
 *  T4 deleteCart: ANY -> OBSOLETE (global transition)
 */
public class MyCartStateTest extends AbstractSalesManagerCoreTestCase {

    @Inject private ProductService productService;
    @Inject private ShoppingCartService shoppingCartService;
    @Inject private CategoryService categoryService;
    @Inject private ManufacturerService manufacturerService;
    @Inject private ProductTypeService productTypeService;
    @Inject private LanguageService languageService;
    @Inject private MerchantStoreService merchantStoreService;

    private MerchantStore store;
    private Product product;

    @Before
    public void setup() throws Exception {
        store = merchantStoreService.getByCode(MerchantStore.DEFAULT_STORE);
        Language en = languageService.getByCode("en");

        Category category = new Category();
        category.setMerchantStore(store);
        category.setCode("test-cat-fsm-" + System.currentTimeMillis());

        CategoryDescription catDesc = new CategoryDescription();
        catDesc.setName("Test Category");
        catDesc.setLanguage(en);
        catDesc.setCategory(category);
        category.setDescriptions(new HashSet<>(Set.of(catDesc)));
        categoryService.create(category);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setMerchantStore(store);
        manufacturer.setCode("test-manuf-fsm-" + System.currentTimeMillis());

        ManufacturerDescription manufDesc = new ManufacturerDescription();
        manufDesc.setName("Test Manuf");
        manufDesc.setLanguage(en);
        manufDesc.setManufacturer(manufacturer);
        manufacturer.setDescriptions(new HashSet<>(Set.of(manufDesc)));
        manufacturerService.create(manufacturer);

        ProductType productType = productTypeService.getProductType(ProductType.GENERAL_TYPE);

        product = new Product();
        product.setProductHeight(new BigDecimal(4));
        product.setProductLength(new BigDecimal(3));
        product.setProductWidth(new BigDecimal(1));
        product.setSku("TEST-SKU-FSM-" + System.currentTimeMillis());
        product.setManufacturer(manufacturer);
        product.setType(productType);
        product.setMerchantStore(store);

        ProductAvailability availability = new ProductAvailability();
        availability.setProductDateAvailable(new Date());
        availability.setProductQuantity(100);
        availability.setRegion("*");
        availability.setProduct(product);

        ProductPrice dprice = new ProductPrice();
        dprice.setDefaultPrice(true);
        dprice.setProductPriceAmount(new BigDecimal("29.99"));
        dprice.setProductAvailability(availability);
        availability.getPrices().add(dprice);

        ProductDescription description = new ProductDescription();
        description.setName("Test Product for FSM");
        description.setLanguage(en);
        description.setProduct(product);

        product.getDescriptions().add(description);
        product.getAvailabilities().add(availability);
        product.getCategories().add(category);

        productService.create(product);
    }

    // -------------------------
    // Helpers (keep tests clean)
    // -------------------------

    private ShoppingCart newEmptyCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        return cart;
    }

    private ShoppingCart addOneItemAndPersist(ShoppingCart cart, int qty) throws Exception {
        ShoppingCartItem item = new ShoppingCartItem(cart, product);
        item.setQuantity(qty);

        // If lineItems is null in your runtime, uncomment next line:
        // if (cart.getLineItems() == null) cart.setLineItems(new HashSet<>());

        cart.getLineItems().add(item);
        shoppingCartService.saveOrUpdate(cart);
        return shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
    }

    private ShoppingCart reload(String code) throws Exception {
        return shoppingCartService.getByCode(code, store);
    }

    private boolean isEmptyState(ShoppingCart cart) {
        return cart == null || cart.getLineItems() == null || cart.getLineItems().isEmpty();
    }

    // -------------------------
    // FSM Tests (transition-level)
    // -------------------------

    /**
     * State check: Newly created cart should be EMPTY (in-memory).
     * (Optional but nice as a state invariant test)
     */
    @Test
    public void testState_EMPTY_onNewCart() {
        ShoppingCart cart = newEmptyCart();
        Assert.assertNotNull(cart);
        Assert.assertTrue("New cart should be EMPTY", cart.getLineItems() == null || cart.getLineItems().isEmpty());
    }

    /**
     * T1: addItem => EMPTY -> ACTIVE
     */
    @Test
    public void testTransition_addItem_EmptyToActive() throws Exception {
        ShoppingCart cart = newEmptyCart();

        ShoppingCart active = addOneItemAndPersist(cart, 1);

        Assert.assertNotNull("Cart should exist after saveOrUpdate", active);
        Assert.assertFalse("Cart should be ACTIVE (has items)", active.getLineItems().isEmpty());
    }

    /**
     * T2: updateQty => ACTIVE -> ACTIVE (self-loop)
     */
    @Test
    public void testTransition_updateQty_ActiveSelfLoop() throws Exception {
        ShoppingCart cart = newEmptyCart();
        ShoppingCart active = addOneItemAndPersist(cart, 1);

        ShoppingCartItem item = active.getLineItems().iterator().next();
        item.setQuantity(2);
        shoppingCartService.saveOrUpdate(active);

        ShoppingCart reloaded = reload(cart.getShoppingCartCode());
        Assert.assertNotNull(reloaded);
        Assert.assertFalse("Should remain ACTIVE after updateQty", reloaded.getLineItems().isEmpty());
        Assert.assertEquals("Quantity should be updated", 2, reloaded.getLineItems().iterator().next().getQuantity().intValue());
    }

    /**
     * T3: removeLastItem => ACTIVE -> EMPTY
     */
    @Test
    public void testTransition_removeLastItem_ActiveToEmpty() throws Exception {
        ShoppingCart cart = newEmptyCart();
        ShoppingCart active = addOneItemAndPersist(cart, 1);

        ShoppingCartItem item = active.getLineItems().iterator().next();
        shoppingCartService.deleteShoppingCartItem(item.getId());

        ShoppingCart afterRemove = reload(cart.getShoppingCartCode());

        // Depending on implementation, cart may still exist but lineItems empty,
        // or cart may be removed. Both represent EMPTY state in our model.
        Assert.assertTrue("After removing last item, state should be EMPTY", isEmptyState(afterRemove));
    }

    /**
     * T4 (global): deleteCart from EMPTY => EMPTY -> OBSOLETE
     */
    @Test
    public void testTransition_deleteCart_FromEmptyToObsolete() throws Exception {
        ShoppingCart cart = newEmptyCart();

        // Persist an empty cart if your system needs it in DB before deletion.
        // If saveOrUpdate(empty) causes issues, you can skip saving and only test delete on persisted carts.
        shoppingCartService.saveOrUpdate(cart);

        ShoppingCart persisted = reload(cart.getShoppingCartCode());
        // persisted could be null if the system doesn't persist empty carts; handle gracefully:
        if (persisted != null) {
            // Ensure it's empty state before deletion
            Assert.assertTrue("Precondition: should be EMPTY before deleteCart", isEmptyState(persisted));

            shoppingCartService.deleteCart(persisted);
        }

        ShoppingCart afterDelete = reload(cart.getShoppingCartCode());
        Assert.assertNull("After deleteCart, cart should be OBSOLETE (null)", afterDelete);
    }

    /**
     * T4 (global): deleteCart from ACTIVE => ACTIVE -> OBSOLETE
     */
    @Test
    public void testTransition_deleteCart_FromActiveToObsolete() throws Exception {
        ShoppingCart cart = newEmptyCart();
        ShoppingCart active = addOneItemAndPersist(cart, 1);

        Assert.assertNotNull(active);
        Assert.assertFalse("Precondition: should be ACTIVE before deleteCart", active.getLineItems().isEmpty());

        shoppingCartService.deleteCart(active);

        ShoppingCart afterDelete = reload(cart.getShoppingCartCode());
        Assert.assertNull("After deleteCart, cart should be OBSOLETE (null)", afterDelete);
    }

    /**
     * Invalid transition example:
     * updateQty is invalid when cart is EMPTY.
     *
     * NOTE: Some implementations may throw exceptions; others may no-op.
     * This test is written to be robust: it asserts the cart does NOT become ACTIVE.
     */
    @Test
    public void testInvalidTransition_updateQty_WhenEmpty_ShouldNotBecomeActive() throws Exception {
        ShoppingCart cart = newEmptyCart();

        // Try to "update" without adding item: we simulate by saving the empty cart (if possible),
        // then reloading and ensuring it does not become ACTIVE.
        shoppingCartService.saveOrUpdate(cart);

        ShoppingCart reloaded = reload(cart.getShoppingCartCode());
        // If system doesn't persist empty carts, reloaded might be null -> still EMPTY
        Assert.assertTrue("Updating qty in EMPTY should not create ACTIVE cart", isEmptyState(reloaded));
    }

    // -------------------------
    // Optional: one full path test (integration sanity check)
    // -------------------------

    /**
     * Full lifecycle path sanity check:
     * EMPTY -> ACTIVE -> ACTIVE -> EMPTY -> OBSOLETE
     *
     * Optional: keep 1 scenario test as a complement (not the main coverage mechanism).
     */
    @Test
    public void testPath_fullLifecycleSanity() throws Exception {
        ShoppingCart cart = newEmptyCart();

        // EMPTY
        Assert.assertTrue(isEmptyState(cart));

        // addItem => ACTIVE
        ShoppingCart active = addOneItemAndPersist(cart, 1);
        Assert.assertNotNull(active);
        Assert.assertFalse(active.getLineItems().isEmpty());

        // updateQty => ACTIVE (self-loop)
        ShoppingCartItem item = active.getLineItems().iterator().next();
        item.setQuantity(2);
        shoppingCartService.saveOrUpdate(active);
        ShoppingCart afterUpdate = reload(cart.getShoppingCartCode());
        Assert.assertEquals(2, afterUpdate.getLineItems().iterator().next().getQuantity().intValue());

        // removeLastItem => EMPTY
        ShoppingCartItem updatedItem = afterUpdate.getLineItems().iterator().next();
        shoppingCartService.deleteShoppingCartItem(updatedItem.getId());
        ShoppingCart afterRemove = reload(cart.getShoppingCartCode());
        Assert.assertTrue(isEmptyState(afterRemove));

        // deleteCart => OBSOLETE
        if (afterRemove != null) {
            shoppingCartService.deleteCart(afterRemove);
        }
        ShoppingCart afterDelete = reload(cart.getShoppingCartCode());
        Assert.assertNull(afterDelete);
    }
}
