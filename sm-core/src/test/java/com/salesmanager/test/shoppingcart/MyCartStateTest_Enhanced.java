package com.salesmanager.test.shoppingcart;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.manufacturer.ManufacturerService;
import com.salesmanager.core.business.services.catalog.product.type.ProductTypeService;
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

/**
 * Enhanced FSM State Transition Test for Shopping Cart
 * Comprehensive test suite with 16 test cases covering:
 * - 3 State verification tests
 * - 5 Transition tests
 * - 4 Boundary condition tests
 * - 2 Invalid transition tests
 * - 2 Complete scenario tests
 * 
 * @author Yuqian Chiu
 */
public class MyCartStateTest_Enhanced extends AbstractSalesManagerCoreTestCase {

    @Inject private ProductService productService;
    @Inject private ShoppingCartService shoppingCartService;
    @Inject private CategoryService categoryService;
    @Inject private ManufacturerService manufacturerService;
    @Inject private ProductTypeService productTypeService;
    @Inject private LanguageService languageService;
    @Inject private MerchantStoreService merchantStoreService;

    private MerchantStore store;
    private Product product1;
    private Product product2;

    @Before
    public void setup() throws Exception {
        store = merchantStoreService.getByCode(MerchantStore.DEFAULT_STORE);
        Language en = languageService.getByCode("en");

        // Create category
        Category category = new Category();
        category.setMerchantStore(store);
        category.setCode("test-cat-cart-" + System.currentTimeMillis());

        CategoryDescription catDesc = new CategoryDescription();
        catDesc.setName("Test Category");
        catDesc.setLanguage(en);
        catDesc.setCategory(category);
        category.setDescriptions(new HashSet<>(Set.of(catDesc)));
        categoryService.create(category);

        // Create manufacturer
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setMerchantStore(store);
        manufacturer.setCode("test-manuf-cart-" + System.currentTimeMillis());

        ManufacturerDescription manufDesc = new ManufacturerDescription();
        manufDesc.setName("Test Manufacturer");
        manufDesc.setLanguage(en);
        manufDesc.setManufacturer(manufacturer);
        manufacturer.setDescriptions(new HashSet<>(Set.of(manufDesc)));
        manufacturerService.create(manufacturer);

        ProductType productType = productTypeService.getProductType(ProductType.GENERAL_TYPE);

        // Create Product 1
        product1 = createProduct("CART-PROD-1", "Test Product 1", new BigDecimal(19.99), 
                                  manufacturer, productType, category, en);

        // Create Product 2 for multi-item tests
        product2 = createProduct("CART-PROD-2", "Test Product 2", new BigDecimal(29.99), 
                                  manufacturer, productType, category, en);
    }

    private Product createProduct(String sku, String name, BigDecimal price, 
                                   Manufacturer manufacturer, ProductType productType,
                                   Category category, Language language) throws Exception {
        Product product = new Product();
        product.setProductHeight(new BigDecimal(4));
        product.setProductLength(new BigDecimal(3));
        product.setProductWidth(new BigDecimal(1));
        product.setSku(sku + "-" + System.currentTimeMillis());
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
        dprice.setProductPriceAmount(price);
        dprice.setProductAvailability(availability);
        availability.getPrices().add(dprice);

        ProductDescription description = new ProductDescription();
        description.setName(name);
        description.setLanguage(language);
        description.setProduct(product);

        product.getDescriptions().add(description);
        product.getAvailabilities().add(availability);
        product.getCategories().add(category);

        productService.create(product);
        return product;
    }

    // ========== Category 1: State Verification Tests ==========

    @Test
    public void testState1_EmptyCart() throws Exception {
        System.out.println("=== Test 1: EMPTY State Verification ===");
        
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());

        Assert.assertNotNull("Cart should be created", cart);
        Assert.assertTrue("Cart should be EMPTY", 
                         cart.getLineItems() == null || cart.getLineItems().isEmpty());
        
        System.out.println("✓ State EMPTY verified");
    }

    @Test
    public void testState2_ActiveCart() throws Exception {
        System.out.println("=== Test 2: ACTIVE State Verification ===");
        
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(cart, product1);
        item.setQuantity(1);
        cart.getLineItems().add(item);
        shoppingCartService.saveOrUpdate(cart);

        ShoppingCart activeCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        Assert.assertNotNull("Cart should exist", activeCart);
        Assert.assertFalse("Cart should be ACTIVE", activeCart.getLineItems().isEmpty());
        Assert.assertEquals("Should have 1 item", 1, activeCart.getLineItems().size());
        
        System.out.println("✓ State ACTIVE verified");
    }

    @Test
    public void testState3_ObsoleteCart() throws Exception {
        System.out.println("=== Test 3: OBSOLETE State Verification ===");
        
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        
        ShoppingCartItem item = new ShoppingCartItem(cart, product1);
        item.setQuantity(1);
        cart.getLineItems().add(item);
        shoppingCartService.saveOrUpdate(cart);
        
        shoppingCartService.deleteCart(cart);

        ShoppingCart obsoleteCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        Assert.assertNull("Cart should be OBSOLETE (null)", obsoleteCart);
        
        System.out.println("✓ State OBSOLETE verified");
    }

    // ========== Category 2: Transition Tests ==========

    @Test
    public void testTransition1_EmptyToActive_AddItem() throws Exception {
        System.out.println("=== Test 4: Transition EMPTY → ACTIVE ===");
        
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        
        // Verify EMPTY
        Assert.assertTrue("Initial state: EMPTY", 
                         cart.getLineItems() == null || cart.getLineItems().isEmpty());
        
        // Transition: Add item
        ShoppingCartItem item = new ShoppingCartItem(cart, product1);
        item.setQuantity(2);
        cart.getLineItems().add(item);
        shoppingCartService.saveOrUpdate(cart);
        
        // Verify ACTIVE
        ShoppingCart activeCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        Assert.assertFalse("New state: ACTIVE", activeCart.getLineItems().isEmpty());
        
        System.out.println("✓ Transition EMPTY → ACTIVE successful");
    }

    @Test
    public void testTransition2_ActiveSelfLoop_UpdateQuantity() throws Exception {
        System.out.println("=== Test 5: Transition ACTIVE → ACTIVE (Self-Loop) ===");
        
        // Setup: Create ACTIVE cart
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        
        ShoppingCartItem item = new ShoppingCartItem(cart, product1);
        item.setQuantity(1);
        cart.getLineItems().add(item);
        shoppingCartService.saveOrUpdate(cart);
        
        // Transition: Update quantity (self-loop)
        ShoppingCart activeCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        ShoppingCartItem itemToUpdate = activeCart.getLineItems().iterator().next();
        itemToUpdate.setQuantity(5);
        shoppingCartService.saveOrUpdate(activeCart);
        
        // Verify still ACTIVE with updated quantity
        ShoppingCart updatedCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        Assert.assertFalse("State: still ACTIVE", updatedCart.getLineItems().isEmpty());
        Assert.assertEquals("Quantity updated", 5, 
                           updatedCart.getLineItems().iterator().next().getQuantity().intValue());
        
        System.out.println("✓ Transition ACTIVE → ACTIVE successful");
    }

    @Test
    public void testTransition3_ActiveToEmpty_RemoveLastItem() throws Exception {
        System.out.println("=== Test 6: Transition ACTIVE → EMPTY ===");
        
        // Setup: Create ACTIVE cart
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        
        ShoppingCartItem item = new ShoppingCartItem(cart, product1);
        item.setQuantity(1);
        cart.getLineItems().add(item);
        shoppingCartService.saveOrUpdate(cart);
        
        // Transition: Remove last item
        ShoppingCart activeCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        ShoppingCartItem itemToRemove = activeCart.getLineItems().iterator().next();
        shoppingCartService.deleteShoppingCartItem(itemToRemove.getId());
        
        // Verify EMPTY
        ShoppingCart emptyCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        if (emptyCart != null) {
            Assert.assertTrue("State: EMPTY", 
                             emptyCart.getLineItems() == null || emptyCart.getLineItems().isEmpty());
        }
        
        System.out.println("✓ Transition ACTIVE → EMPTY successful");
    }

    @Test
    public void testTransition4_EmptyToObsolete_DeleteEmptyCart() throws Exception {
        System.out.println("=== Test 7: Transition EMPTY → OBSOLETE ===");
        
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        shoppingCartService.saveOrUpdate(cart);
        
        // Verify EMPTY
        ShoppingCart emptyCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
            Assert.assertNotNull("Cart should exist", emptyCart);
            Assert.assertTrue("Initial state: EMPTY", 
                     emptyCart.getLineItems() == null || emptyCart.getLineItems().isEmpty());
        
        // Transition: Delete empty cart
        shoppingCartService.deleteCart(emptyCart);
        
        // Verify OBSOLETE
        ShoppingCart obsoleteCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        Assert.assertNull("State: OBSOLETE", obsoleteCart);
        
        System.out.println("✓ Transition EMPTY → OBSOLETE successful");
    }

    @Test
    public void testTransition5_ActiveToObsolete_DeleteActiveCart() throws Exception {
        System.out.println("=== Test 8: Transition ACTIVE → OBSOLETE ===");
        
        // Setup: Create ACTIVE cart
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        
        ShoppingCartItem item = new ShoppingCartItem(cart, product1);
        item.setQuantity(1);
        cart.getLineItems().add(item);
        shoppingCartService.saveOrUpdate(cart);
        
        // Verify ACTIVE
        ShoppingCart activeCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        Assert.assertFalse("Initial state: ACTIVE", activeCart.getLineItems().isEmpty());
        
        // Transition: Delete active cart
        shoppingCartService.deleteCart(activeCart);
        
        // Verify OBSOLETE
        ShoppingCart obsoleteCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        Assert.assertNull("State: OBSOLETE", obsoleteCart);
        
        System.out.println("✓ Transition ACTIVE → OBSOLETE successful");
    }

    // ========== Category 3: Boundary Condition Tests ==========

    @Test
    public void testBoundary1_MaxQuantityInCart() throws Exception {
        System.out.println("=== Test 9: Boundary - Maximum Quantity ===");
        
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        
        ShoppingCartItem item = new ShoppingCartItem(cart, product1);
        item.setQuantity(100); // Maximum available quantity
        cart.getLineItems().add(item);
        shoppingCartService.saveOrUpdate(cart);
        
        ShoppingCart savedCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        Assert.assertEquals("Max quantity accepted", 100, 
                           savedCart.getLineItems().iterator().next().getQuantity().intValue());
        
        System.out.println("✓ Boundary test: Max quantity passed");
    }

    @Test
    public void testBoundary2_MultipleItemsInCart() throws Exception {
        System.out.println("=== Test 10: Boundary - Multiple Items ===");
        
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        
        ShoppingCartItem item1 = new ShoppingCartItem(cart, product1);
        item1.setQuantity(2);
        cart.getLineItems().add(item1);
        
        ShoppingCartItem item2 = new ShoppingCartItem(cart, product2);
        item2.setQuantity(3);
        cart.getLineItems().add(item2);
        
        shoppingCartService.saveOrUpdate(cart);
        
        ShoppingCart savedCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        Assert.assertEquals("Should have 2 items", 2, savedCart.getLineItems().size());
        
        System.out.println("✓ Boundary test: Multiple items passed");
    }

    @Test
    public void testBoundary3_ZeroQuantity() throws Exception {
        System.out.println("=== Test 11: Boundary - Zero Quantity ===");
        
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        
        ShoppingCartItem item = new ShoppingCartItem(cart, product1);
        item.setQuantity(0);
        cart.getLineItems().add(item);
        
        try {
            shoppingCartService.saveOrUpdate(cart);
            ShoppingCart savedCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
            
            if (savedCart != null && !savedCart.getLineItems().isEmpty()) {
                System.out.println("⚠ Warning: BUG-CART-02 - System allows quantity = 0");
            } else {
                System.out.println("✓ Zero quantity correctly handled");
            }
        } catch (Exception e) {
            System.out.println("✓ Zero quantity rejected as expected");
        }
    }

    @Test
    public void testBoundary4_NegativeQuantity() throws Exception {
        System.out.println("=== Test 12: Boundary - Negative Quantity ===");
        
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        
        ShoppingCartItem item = new ShoppingCartItem(cart, product1);
        item.setQuantity(-5);
        cart.getLineItems().add(item);
        
        try {
            shoppingCartService.saveOrUpdate(cart);
            ShoppingCart savedCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
            
            if (savedCart != null && !savedCart.getLineItems().isEmpty()) {
                System.out.println("⚠ Warning: BUG-CART-01 - System allows negative quantity");
            } else {
                System.out.println("✓ Negative quantity correctly rejected");
            }
        } catch (Exception e) {
            System.out.println("✓ Negative quantity rejected as expected");
        }
    }

    // ========== Category 4: Invalid Transition Tests ==========

    @Test
    public void testInvalid1_UpdateQuantityInEmptyCart() throws Exception {
        System.out.println("=== Test 13: Invalid - Update in EMPTY State ===");
        
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        shoppingCartService.saveOrUpdate(cart);
        
        ShoppingCart emptyCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        
        try {
            if (emptyCart.getLineItems().isEmpty()) {
                // Attempt invalid operation
                ShoppingCartItem item = new ShoppingCartItem(emptyCart, product1);
                item.setQuantity(1);
                item.setId(999L); // Non-existent ID
                shoppingCartService.saveOrUpdate(emptyCart);
                
                System.out.println("⚠ Warning: BUG-CART-03 - Update in EMPTY state allowed");
            }
        } catch (Exception e) {
            System.out.println("✓ Invalid operation correctly rejected");
        }
    }

    @Test
    public void testInvalid2_RemoveItemFromEmptyCart() throws Exception {
        System.out.println("=== Test 14: Invalid - Remove from EMPTY State ===");
        
        try {
            shoppingCartService.deleteShoppingCartItem(999999L); // Non-existent ID
            System.out.println("⚠ Warning: Remove from EMPTY should fail");
        } catch (Exception e) {
            System.out.println("✓ Invalid operation correctly rejected");
        }
    }

    // ========== Category 5: Complete Scenario Tests ==========

    @Test
    public void testScenario1_CompleteLifecycle() throws Exception {
        System.out.println("=== Test 15: Complete Lifecycle Scenario ===");
        
        // State 1: EMPTY
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        Assert.assertTrue("State 1: EMPTY", 
                         cart.getLineItems() == null || cart.getLineItems().isEmpty());
        System.out.println("  ✓ State 1: EMPTY");
        
        // Transition: EMPTY → ACTIVE
        ShoppingCartItem item = new ShoppingCartItem(cart, product1);
        item.setQuantity(1);
        cart.getLineItems().add(item);
        shoppingCartService.saveOrUpdate(cart);
        
        ShoppingCart activeCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        Assert.assertFalse("State 2: ACTIVE", activeCart.getLineItems().isEmpty());
        System.out.println("  ✓ State 2: ACTIVE");
        
        // Transition: ACTIVE → ACTIVE (self-loop)
        ShoppingCartItem itemToUpdate = activeCart.getLineItems().iterator().next();
        itemToUpdate.setQuantity(3);
        shoppingCartService.saveOrUpdate(activeCart);
        System.out.println("  ✓ Self-loop: Quantity updated");
        
        // Transition: ACTIVE → EMPTY
        shoppingCartService.deleteShoppingCartItem(itemToUpdate.getId());
        ShoppingCart emptyCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        if (emptyCart != null) {
            Assert.assertTrue("State 3: EMPTY (returned)", 
                             emptyCart.getLineItems() == null || emptyCart.getLineItems().isEmpty());
            System.out.println("  ✓ State 3: EMPTY (returned)");
            
            // Transition: EMPTY → OBSOLETE
            shoppingCartService.deleteCart(emptyCart);
        }
        
        ShoppingCart obsoleteCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        Assert.assertNull("State 4: OBSOLETE", obsoleteCart);
        System.out.println("  ✓ State 4: OBSOLETE");
        
        System.out.println("✓ Complete lifecycle test passed");
    }

    @Test
    public void testScenario2_MultipleOperations() throws Exception {
        System.out.println("=== Test 16: Multiple Operations Scenario ===");
        
        ShoppingCart cart = new ShoppingCart();
        cart.setMerchantStore(store);
        cart.setShoppingCartCode(UUID.randomUUID().toString());
        
        // Add first item
        ShoppingCartItem item1 = new ShoppingCartItem(cart, product1);
        item1.setQuantity(2);
        cart.getLineItems().add(item1);
        shoppingCartService.saveOrUpdate(cart);
        System.out.println("  ✓ Added product 1 (qty: 2)");
        
        // Add second item
        ShoppingCart cart1 = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        ShoppingCartItem item2 = new ShoppingCartItem(cart1, product2);
        item2.setQuantity(1);
        cart1.getLineItems().add(item2);
        shoppingCartService.saveOrUpdate(cart1);
        System.out.println("  ✓ Added product 2 (qty: 1)");
        
        // Update first item quantity
        ShoppingCart cart2 = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        for (ShoppingCartItem item : cart2.getLineItems()) {
            if (item.getProduct().getId().equals(product1.getId())) {
                item.setQuantity(5);
                break;
            }
        }
        shoppingCartService.saveOrUpdate(cart2);
        System.out.println("  ✓ Updated product 1 (qty: 2 → 5)");
        
        // Verify final state
        ShoppingCart finalCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
        Assert.assertEquals("Should have 2 items", 2, finalCart.getLineItems().size());
        
        // Delete cart
        shoppingCartService.deleteCart(finalCart);
        Assert.assertNull("Cart deleted", 
                         shoppingCartService.getByCode(cart.getShoppingCartCode(), store));
        
        System.out.println("✓ Multiple operations scenario passed");
    }
}
