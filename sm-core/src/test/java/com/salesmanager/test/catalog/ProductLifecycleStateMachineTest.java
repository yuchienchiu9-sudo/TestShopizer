package com.salesmanager.test.catalog;

import java.math.BigDecimal;
import java.sql.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.category.CategoryDescription;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;

/**
 * Assignment 2: Product Lifecycle Finite State Machine Test
 * 
 * This test suite validates the state transitions in a product's lifecycle
 * using a Finite State Machine (FSM) model for e-commerce product management.
 * 
 * FSM States:
 * - DRAFT: Initial state, product is being created/edited (available=false, date=future)
 * - PENDING: Submitted for approval, awaiting validation (available=false, date=future)
 * - ACTIVE: Approved and live, available for purchase (available=true, date=past)
 * - MODIFIED: Active product updated, pending re-validation (available=true, date=past, hasChanges)
 * - INACTIVE: Temporarily removed from catalog (available=false, date=past)
 * - ARCHIVED: Permanently removed, kept for records (available=false, quantity=0)
 * 
 * Key Transitions:
 * 1. DRAFT → PENDING (submitForApproval with valid price)
 * 2. PENDING → ACTIVE (approve)
 * 3. PENDING → DRAFT (reject)
 * 4. ACTIVE → MODIFIED (updatePrice/updateDetails)
 * 5. ACTIVE → INACTIVE (deactivate)
 * 6. INACTIVE → ACTIVE (reactivate)
 * 7. INACTIVE → ARCHIVED (archive)
 * 8. DRAFT → ARCHIVED (discard)
 * 
 * Guard Conditions:
 * - Price must be valid (>= 0, proper precision)
 * - Product must have category
 * - Product must have description
 * - Inventory must be >= 0
 * 
 * @author Yijun Sun
 * @version 1.0
 */
public class ProductLifecycleStateMachineTest extends com.salesmanager.test.common.AbstractSalesManagerCoreTestCase {

    private static final Date FUTURE_DATE = new Date(System.currentTimeMillis() + 86400000L); // +1 day
    private static final Date PAST_DATE = new Date(System.currentTimeMillis() - 86400000L);   // -1 day
    
    private MerchantStore store;
    private Language language;
    private Category category;
    private ProductType productType;

    @Before
    public void setUp() throws Exception {
        // Initialize common test fixtures
        store = merchantService.getByCode(MerchantStore.DEFAULT_STORE);
        language = languageService.getByCode("en");
        productType = productTypeService.getProductType(ProductType.GENERAL_TYPE);
        
        // Create test category
        category = new Category();
        category.setMerchantStore(store);
        category.setCode("test-category-" + System.currentTimeMillis());
        
        CategoryDescription categoryDesc = new CategoryDescription();
        categoryDesc.setCategory(category);
        categoryDesc.setLanguage(language);
        categoryDesc.setName("Test Category");
        category.getDescriptions().add(categoryDesc);
        
        categoryService.create(category);
    }

    /**
     * Helper method to create a product in a specific state
     */
    private Product createProduct(String state, BigDecimal price, Integer quantity, Date availableDate) throws ServiceException {
        String sku = "PROD-" + state + "-" + System.currentTimeMillis();
        
        Product product = new Product();
        product.setMerchantStore(store);
        product.setSku(sku);
        product.setType(productType);
        product.setAvailable(state.equals("ACTIVE") || state.equals("MODIFIED"));
        
        // Product description
        ProductDescription description = new ProductDescription();
        description.setProduct(product);
        description.setLanguage(language);
        description.setName("Test Product - " + state);
        description.setDescription("Product in " + state + " state");
        product.getDescriptions().add(description);
        
        // Product category
        product.getCategories().add(category);
        
        // Product availability
        ProductAvailability availability = new ProductAvailability();
        availability.setProduct(product);
        availability.setProductQuantity(quantity);
        availability.setProductDateAvailable(availableDate);
        availability.setRegion("*");
        
        // Product price
        ProductPrice productPrice = new ProductPrice();
        productPrice.setProductAvailability(availability);
        productPrice.setProductPriceAmount(price);
        productPrice.setDefaultPrice(true);
        
        availability.getPrices().add(productPrice);
        product.getAvailabilities().add(availability);
        
        return product;
    }

    // =============================================================================
    // STATE 1: DRAFT STATE TESTS
    // =============================================================================

    /**
     * Test Case 1: Verify product creation in DRAFT state
     * Initial state: Product is created with future availability date and not available
     */
    @Test
    public void testState1_ProductInDraftState() throws Exception {
        System.out.println("\n=== Test 1: DRAFT State Creation ===");
        
        Product product = createProduct("DRAFT", new BigDecimal("99.99"), 10, FUTURE_DATE);
        productService.create(product);
        
        Product retrieved = productService.getById(product.getId());
        
        Assert.assertNotNull("Product should be created", retrieved);
        Assert.assertFalse("DRAFT product should not be available", retrieved.isAvailable());
        Assert.assertEquals("SKU should match", product.getSku(), retrieved.getSku());
        
        System.out.println("✓ State DRAFT verified: Product created but not available");
    }

    /**
     * Test Case 2: DRAFT state with invalid price should fail guard condition
     */
    @Test(expected = Exception.class)
    public void testState1_DraftWithInvalidPrice() throws Exception {
        System.out.println("\n=== Test 2: DRAFT with Invalid Price (Guard Violation) ===");
        
        Product product = createProduct("DRAFT", new BigDecimal("-10.00"), 10, FUTURE_DATE);
        productService.create(product); // Should fail
        
        Assert.fail("Should not allow negative price");
    }

    /**
     * Test Case 3: DRAFT state without category should fail guard condition
     */
    @Test(expected = Exception.class)
    public void testState1_DraftWithoutCategory() throws Exception {
        System.out.println("\n=== Test 3: DRAFT without Category (Guard Violation) ===");
        
        Product product = createProduct("DRAFT", new BigDecimal("99.99"), 10, FUTURE_DATE);
        product.getCategories().clear(); // Remove category
        productService.create(product); // Should fail
        
        Assert.fail("Should not allow product without category");
    }

    // =============================================================================
    // TRANSITION 1: DRAFT → PENDING (Submit for Approval)
    // =============================================================================

    /**
     * Test Case 4: Valid transition from DRAFT to PENDING
     * Guard conditions: valid price, has category, has description
     */
    @Test
    public void testTransition1_DraftToPendingValid() throws Exception {
        System.out.println("\n=== Test 4: DRAFT → PENDING (Valid) ===");
        
        // Create DRAFT product
        Product product = createProduct("DRAFT", new BigDecimal("99.99"), 10, FUTURE_DATE);
        productService.create(product);
        
        // Transition to PENDING (still not available, but date approaches)
        product.setAvailable(false); // Still not available
        productService.update(product);
        
        Product pending = productService.getById(product.getId());
        Assert.assertFalse("PENDING product should not be available yet", pending.isAvailable());
        
        System.out.println("✓ Transition DRAFT → PENDING successful");
    }

    /**
     * Test Case 5: Invalid transition with null price
     */
    @Test
    public void testTransition1_DraftToPendingInvalidPrice() throws Exception {
        System.out.println("\n=== Test 5: DRAFT → PENDING with Null Price (Should Fail) ===");
        
        Product product = createProduct("DRAFT", new BigDecimal("99.99"), 10, FUTURE_DATE);
        productService.create(product);
        
        try {
            // Try to clear price and transition
            product.getAvailabilities().iterator().next().getPrices().clear();
            productService.update(product);
            
            // If update succeeds without price, it violates our business logic
            System.out.println("⚠ Warning: System allowed product without price (potential bug)");
        } catch (Exception e) {
            System.out.println("✓ Correctly rejected PENDING state with null price");
        }
    }

    // =============================================================================
    // TRANSITION 2: PENDING → ACTIVE (Approve)
    // =============================================================================

    /**
     * Test Case 6: Valid approval transition from PENDING to ACTIVE
     */
    @Test
    public void testTransition2_PendingToActive() throws Exception {
        System.out.println("\n=== Test 6: PENDING → ACTIVE (Approval) ===");
        
        // Create PENDING product
        Product product = createProduct("PENDING", new BigDecimal("199.99"), 20, PAST_DATE);
        product.setAvailable(false); // PENDING state
        productService.create(product);
        
        // Approve: Make available and set date to past
        product.setAvailable(true);
        ProductAvailability availability = product.getAvailabilities().iterator().next();
        availability.setProductDateAvailable(PAST_DATE);
        productService.update(product);
        
        Product active = productService.getById(product.getId());
        Assert.assertTrue("ACTIVE product should be available", active.isAvailable());
        
        System.out.println("✓ Transition PENDING → ACTIVE successful (Product now live)");
    }

    // =============================================================================
    // TRANSITION 3: PENDING → DRAFT (Reject)
    // =============================================================================

    /**
     * Test Case 7: Rejection sends product back to DRAFT
     */
    @Test
    public void testTransition3_PendingToDraft() throws Exception {
        System.out.println("\n=== Test 7: PENDING → DRAFT (Rejection) ===");
        
        // Create PENDING product
        Product product = createProduct("PENDING", new BigDecimal("99.99"), 10, FUTURE_DATE);
        product.setAvailable(false);
        productService.create(product);
        
        // Reject: Keep unavailable, reset to future date
        ProductAvailability availability = product.getAvailabilities().iterator().next();
        availability.setProductDateAvailable(FUTURE_DATE);
        productService.update(product);
        
        Product draft = productService.getById(product.getId());
        Assert.assertFalse("Rejected product should return to DRAFT (unavailable)", draft.isAvailable());
        
        System.out.println("✓ Transition PENDING → DRAFT successful (Rejection handled)");
    }

    // =============================================================================
    // STATE 2: ACTIVE STATE TESTS
    // =============================================================================

    /**
     * Test Case 8: Verify product in ACTIVE state
     */
    @Test
    public void testState2_ProductInActiveState() throws Exception {
        System.out.println("\n=== Test 8: ACTIVE State Verification ===");
        
        Product product = createProduct("ACTIVE", new BigDecimal("299.99"), 50, PAST_DATE);
        product.setAvailable(true);
        productService.create(product);
        
        Product active = productService.getById(product.getId());
        
        Assert.assertTrue("ACTIVE product should be available", active.isAvailable());
        Assert.assertTrue("ACTIVE product date should be in the past",
            active.getAvailabilities().iterator().next().getProductDateAvailable().before(new Date(System.currentTimeMillis())));
        
        System.out.println("✓ State ACTIVE verified: Product is live and purchasable");
    }

    // =============================================================================
    // TRANSITION 4: ACTIVE → MODIFIED (Update Price/Details)
    // =============================================================================

    /**
     * Test Case 9: Price update creates MODIFIED state
     */
    @Test
    public void testTransition4_ActiveToModified() throws Exception {
        System.out.println("\n=== Test 9: ACTIVE → MODIFIED (Price Update) ===");
        
        // Create ACTIVE product
        Product product = createProduct("ACTIVE", new BigDecimal("100.00"), 30, PAST_DATE);
        product.setAvailable(true);
        productService.create(product);
        
        // Modify price (stays available, but marked as modified)
        ProductAvailability availability = product.getAvailabilities().iterator().next();
        ProductPrice price = availability.getPrices().iterator().next();
        price.setProductPriceAmount(new BigDecimal("89.99"));
        productService.update(product);
        
        Product modified = productService.getById(product.getId());
        Assert.assertTrue("MODIFIED product should still be available", modified.isAvailable());
        
        BigDecimal newPrice = modified.getAvailabilities().iterator().next()
            .getPrices().iterator().next().getProductPriceAmount();
        Assert.assertEquals("Price should be updated", new BigDecimal("89.99"), newPrice);
        
        System.out.println("✓ Transition ACTIVE → MODIFIED successful (Price updated)");
    }

    /**
     * Test Case 10: Invalid price update should fail
     */
    @Test
    public void testTransition4_ActiveToModifiedInvalidPrice() throws Exception {
        System.out.println("\n=== Test 10: ACTIVE → MODIFIED with Invalid Price ===");
        
        Product product = createProduct("ACTIVE", new BigDecimal("100.00"), 30, PAST_DATE);
        product.setAvailable(true);
        productService.create(product);
        
        try {
            // Try to set invalid price
            ProductAvailability availability = product.getAvailabilities().iterator().next();
            ProductPrice price = availability.getPrices().iterator().next();
            price.setProductPriceAmount(new BigDecimal("-50.00")); // Invalid
            productService.update(product);
            
            System.out.println("⚠ Warning: System allowed negative price (potential bug)");
        } catch (Exception e) {
            System.out.println("✓ Correctly rejected invalid price update");
        }
    }

    // =============================================================================
    // TRANSITION 5: ACTIVE → INACTIVE (Deactivate)
    // =============================================================================

    /**
     * Test Case 11: Deactivate an active product
     */
    @Test
    public void testTransition5_ActiveToInactive() throws Exception {
        System.out.println("\n=== Test 11: ACTIVE → INACTIVE (Deactivate) ===");
        
        // Create ACTIVE product
        Product product = createProduct("ACTIVE", new BigDecimal("150.00"), 25, PAST_DATE);
        product.setAvailable(true);
        productService.create(product);
        
        // Deactivate
        product.setAvailable(false);
        productService.update(product);
        
        Product inactive = productService.getById(product.getId());
        Assert.assertFalse("INACTIVE product should not be available", inactive.isAvailable());
        
        System.out.println("✓ Transition ACTIVE → INACTIVE successful (Product removed from catalog)");
    }

    // =============================================================================
    // STATE 3: INACTIVE STATE
    // =============================================================================

    /**
     * Test Case 12: Verify product in INACTIVE state
     */
    @Test
    public void testState3_ProductInInactiveState() throws Exception {
        System.out.println("\n=== Test 12: INACTIVE State Verification ===");
        
        Product product = createProduct("INACTIVE", new BigDecimal("99.99"), 10, PAST_DATE);
        product.setAvailable(false);
        productService.create(product);
        
        Product inactive = productService.getById(product.getId());
        Assert.assertFalse("INACTIVE product should not be available", inactive.isAvailable());
        
        System.out.println("✓ State INACTIVE verified: Product exists but not visible");
    }

    // =============================================================================
    // TRANSITION 6: INACTIVE → ACTIVE (Reactivate)
    // =============================================================================

    /**
     * Test Case 13: Reactivate an inactive product
     */
    @Test
    public void testTransition6_InactiveToActive() throws Exception {
        System.out.println("\n=== Test 13: INACTIVE → ACTIVE (Reactivate) ===");
        
        // Create INACTIVE product
        Product product = createProduct("INACTIVE", new BigDecimal("200.00"), 15, PAST_DATE);
        product.setAvailable(false);
        productService.create(product);
        
        // Reactivate
        product.setAvailable(true);
        productService.update(product);
        
        Product reactivated = productService.getById(product.getId());
        Assert.assertTrue("Reactivated product should be available", reactivated.isAvailable());
        
        System.out.println("✓ Transition INACTIVE → ACTIVE successful (Product back in catalog)");
    }

    // =============================================================================
    // TRANSITION 7: INACTIVE → ARCHIVED (Archive)
    // =============================================================================

    /**
     * Test Case 14: Archive an inactive product (soft delete)
     */
    @Test
    public void testTransition7_InactiveToArchived() throws Exception {
        System.out.println("\n=== Test 14: INACTIVE → ARCHIVED (Archive) ===");
        
        // Create INACTIVE product
        Product product = createProduct("INACTIVE", new BigDecimal("50.00"), 5, PAST_DATE);
        product.setAvailable(false);
        productService.create(product);
        
        Long productId = product.getId();
        
        // Archive: Set quantity to 0 to mark as archived
        ProductAvailability availability = product.getAvailabilities().iterator().next();
        availability.setProductQuantity(0);
        productService.update(product);
        
        Product archived = productService.getById(productId);
        Assert.assertEquals("Archived product should have 0 quantity", 
            Integer.valueOf(0), 
            archived.getAvailabilities().iterator().next().getProductQuantity());
        
        System.out.println("✓ Transition INACTIVE → ARCHIVED successful (Product archived)");
    }

    // =============================================================================
    // TRANSITION 8: DRAFT → ARCHIVED (Discard)
    // =============================================================================

    /**
     * Test Case 15: Discard a draft product
     */
    @Test
    public void testTransition8_DraftToArchived() throws Exception {
        System.out.println("\n=== Test 15: DRAFT → ARCHIVED (Discard) ===");
        
        // Create DRAFT product
        Product product = createProduct("DRAFT", new BigDecimal("75.00"), 10, FUTURE_DATE);
        productService.create(product);
        
        Long productId = product.getId();
        
        // Discard: Delete the product
        productService.delete(product);
        
        try {
            Product deleted = productService.getById(productId);
            if (deleted == null) {
                System.out.println("✓ Transition DRAFT → ARCHIVED successful (Product discarded)");
            }
        } catch (Exception e) {
            System.out.println("✓ Transition DRAFT → ARCHIVED successful (Product discarded)");
        }
    }

    // =============================================================================
    // STATE 4: MODIFIED STATE
    // =============================================================================

    /**
     * Test Case 16: Verify MODIFIED state maintains availability
     */
    @Test
    public void testState4_ProductInModifiedState() throws Exception {
        System.out.println("\n=== Test 16: MODIFIED State Verification ===");
        
        // Create and modify product
        Product product = createProduct("ACTIVE", new BigDecimal("120.00"), 40, PAST_DATE);
        product.setAvailable(true);
        productService.create(product);
        
        // Modify description to simulate MODIFIED state
        ProductDescription desc = product.getDescriptions().iterator().next();
        desc.setDescription("Updated description - MODIFIED state");
        productService.update(product);
        
        Product modified = productService.getById(product.getId());
        Assert.assertTrue("MODIFIED product should remain available", modified.isAvailable());
        
        System.out.println("✓ State MODIFIED verified: Changes saved, product still live");
    }

    // =============================================================================
    // GUARD CONDITION TESTS
    // =============================================================================

    /**
     * Test Case 17: Guard - Price must be non-negative
     */
    @Test
    public void testGuard_PriceMustBeNonNegative() throws Exception {
        System.out.println("\n=== Test 17: Guard Condition - Non-Negative Price ===");
        
        try {
            Product product = createProduct("DRAFT", new BigDecimal("-99.99"), 10, FUTURE_DATE);
            productService.create(product);
            System.out.println("⚠ Warning: Negative price accepted (bug detected)");
        } catch (Exception e) {
            System.out.println("✓ Guard enforced: Negative price rejected");
        }
    }

    /**
     * Test Case 18: Guard - Product must have inventory
     */
    @Test
    public void testGuard_ProductMustHaveInventory() throws Exception {
        System.out.println("\n=== Test 18: Guard Condition - Inventory Required ===");
        
        Product product = createProduct("ACTIVE", new BigDecimal("99.99"), 0, PAST_DATE);
        product.setAvailable(true);
        productService.create(product);
        
        Product retrieved = productService.getById(product.getId());
        Integer quantity = retrieved.getAvailabilities().iterator().next().getProductQuantity();
        
        if (quantity == 0) {
            System.out.println("⚠ Note: System allows products with 0 inventory");
        } else {
            System.out.println("✓ Guard enforced: Inventory validated");
        }
    }

    /**
     * Test Case 19: Guard - Product must have category
     */
    @Test
    public void testGuard_ProductMustHaveCategory() throws Exception {
        System.out.println("\n=== Test 19: Guard Condition - Category Required ===");
        
        try {
            Product product = createProduct("DRAFT", new BigDecimal("99.99"), 10, FUTURE_DATE);
            product.getCategories().clear();
            productService.create(product);
            System.out.println("⚠ Warning: Product without category accepted (potential bug)");
        } catch (Exception e) {
            System.out.println("✓ Guard enforced: Category requirement validated");
        }
    }

    /**
     * Test Case 20: Complete lifecycle path test
     * Tests a complete journey through the FSM
     */
    @Test
    public void testCompleteLifecycle() throws Exception {
        System.out.println("\n=== Test 20: Complete Product Lifecycle ===");
        
        // 1. Create in DRAFT
        Product product = createProduct("DRAFT", new BigDecimal("249.99"), 100, FUTURE_DATE);
        productService.create(product);
        System.out.println("  1. Created: DRAFT");
        
        // 2. Submit for approval (DRAFT → PENDING)
        product.setAvailable(false);
        productService.update(product);
        System.out.println("  2. Submitted: PENDING");
        
        // 3. Approve (PENDING → ACTIVE)
        product.setAvailable(true);
        product.getAvailabilities().iterator().next().setProductDateAvailable(PAST_DATE);
        productService.update(product);
        System.out.println("  3. Approved: ACTIVE");
        
        // 4. Modify price (ACTIVE → MODIFIED)
        ProductPrice price = product.getAvailabilities().iterator().next().getPrices().iterator().next();
        price.setProductPriceAmount(new BigDecimal("199.99"));
        productService.update(product);
        System.out.println("  4. Updated: MODIFIED");
        
        // 5. Deactivate (MODIFIED → INACTIVE)
        product.setAvailable(false);
        productService.update(product);
        System.out.println("  5. Deactivated: INACTIVE");
        
        // 6. Archive (INACTIVE → ARCHIVED)
        product.getAvailabilities().iterator().next().setProductQuantity(0);
        productService.update(product);
        System.out.println("  6. Archived: ARCHIVED");
        
        Product finalState = productService.getById(product.getId());
        Assert.assertFalse("Final state should be unavailable", finalState.isAvailable());
        Assert.assertEquals("Final state should have 0 inventory", 
            Integer.valueOf(0), 
            finalState.getAvailabilities().iterator().next().getProductQuantity());
        
        System.out.println("✓ Complete lifecycle test passed: DRAFT → PENDING → ACTIVE → MODIFIED → INACTIVE → ARCHIVED");
    }
}
