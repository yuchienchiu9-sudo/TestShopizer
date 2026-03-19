package com.salesmanager.test.shoppingcart;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

/**
 * Project 1: Extended Partition Testing for Shopping Cart Quantity
 * Enhanced version with comprehensive test coverage
 *
 * Partitions:
 * 1. Valid Range (1-10): Boundary and representative values
 * 2. Invalid Negative: Negative quantities
 * 3. Invalid Zero: Zero quantity
 * 4. Invalid Over Stock: Exceeds available inventory
 * 5. Boundary Values: Edge cases at partition boundaries
 * 6. Extreme Values: Very large numbers
 * 7. Multiple Items: Cart with multiple products
 *
 * Total Test Cases: 13 (expanded from original 4)
 */
public class MyCartQuantityTest extends com.salesmanager.test.common.AbstractSalesManagerCoreTestCase {

    private MerchantStore store;
    private Product product;

    /**
     * SETUP 區域：這裡負責「準備場景」。
     * 每次跑測試之前，它會自動執行，幫你把商店、分類、商品都建好。
     */
    @Before
    public void setup() throws Exception {
        store = merchantService.getByCode(MerchantStore.DEFAULT_STORE);
        Language en = languageService.getByCode("en");
        ProductType generalType = productTypeService.getProductType(ProductType.GENERAL_TYPE);

        // 1. 建立分類 (Category)
        Category shirts = new Category();
        shirts.setMerchantStore(store);
        shirts.setCode("shirts_" + System.currentTimeMillis()); // 加上時間戳記避免重複錯誤
        CategoryDescription shirtsDesc = new CategoryDescription();
        shirtsDesc.setName("Shirts");
        shirtsDesc.setCategory(shirts);
        shirtsDesc.setLanguage(en);
        shirts.setDescriptions(new HashSet<>(Set.of(shirtsDesc)));
        categoryService.create(shirts);

        // 2. 建立製造商 (Manufacturer)
        Manufacturer addidas = new Manufacturer();
        addidas.setMerchantStore(store);
        addidas.setCode("addidas_" + System.currentTimeMillis());
        ManufacturerDescription addidasDesc = new ManufacturerDescription();
        addidasDesc.setLanguage(en);
        addidasDesc.setManufacturer(addidas);
        addidasDesc.setName("Addidas");
        addidas.getDescriptions().add(addidasDesc);
        manufacturerService.create(addidas);

        // 3. 建立商品 (Product)
        product = new Product();
        product.setProductHeight(new BigDecimal(4));
        product.setProductLength(new BigDecimal(3));
        product.setProductWidth(new BigDecimal(1));
        product.setSku("TEST-SKU-" + System.currentTimeMillis());
        product.setManufacturer(addidas);
        product.setType(generalType);
        product.setMerchantStore(store);

        ProductDescription description = new ProductDescription();
        description.setName("Test Shirt");
        description.setLanguage(en);
        description.setProduct(product);
        product.getDescriptions().add(description);
        product.getCategories().add(shirts);

        // 4. 設定庫存 (Availability)
        ProductAvailability availability = new ProductAvailability();
        availability.setProductDateAvailable(new Date());
        availability.setProductQuantity(10); // <--- 注意這裡！我把庫存設為 10
        availability.setRegion("*");
        availability.setProduct(product);

        // 5. 設定價格 (Price)
        ProductPrice dprice = new ProductPrice();
        dprice.setDefaultPrice(true);
        dprice.setProductPriceAmount(new BigDecimal(29.99));
        dprice.setProductAvailability(availability);
        availability.getPrices().add(dprice);
        product.getAvailabilities().add(availability);

        // 存檔
        productService.saveProduct(product);
    }

    // ==========================================
    // 下面開始是你的 4 個 Partition 測試
    // ==========================================
    /**
     * Project 1: Equivalence Partitioning — Shopping Cart Item Quantity
     *
     * Partition definitions (q = quantity, stock = 10):
     * P1 Valid:      1 <= q <= 10
     * P2 Invalid:    q = 0
     * P3 Invalid:    q < 0
     * P4 Invalid:    q > 10
     *
     * Notes:
     * - Boundary values (1, 10, 0, 11) are test *inputs* chosen to cover partitions effectively,
     *   not additional partitions.
     * - Scenario S1 verifies multi-line-item behavior (not a partition).
     */

    /**
     * Partition P1 (Valid Range: 1 <= q <= 10) — Representative value
     * Purpose: Verify that a typical in-range quantity can be added and persisted.
     * Input: q = 5
     * Expected: Cart creation succeeds; retrieved cart contains one line item with quantity = 5.
     */
    @Test
    public void testAddToCart_ValidQuantity() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setSku(product.getSku());

        // 設定數量為 5
        item.setQuantity(5);

        shoppingCart.getLineItems().add(item);
        shoppingCartService.create(shoppingCart);


        ShoppingCart retrievedCart = shoppingCartService.getByCode(shoppingCart.getShoppingCartCode(), store);
        Assert.assertNotNull(retrievedCart);
        Assert.assertEquals(5, retrievedCart.getLineItems().iterator().next().getQuantity().intValue());

        System.out.println("Partition 1 (Valid) Passed: succeed to add five produces");
    }

    /**
     * Partition P3 (Invalid Negative: q < 0) — Representative value
     * Purpose: Ensure negative quantities are rejected by the system.
     * Input: q = -1
     * Expected: shoppingCartService.create(...) throws an Exception; cart is not created.
     */
    @Test(expected = Exception.class)
    public void testAddToCart_NegativeQuantity() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setSku(product.getSku());

        // 設定數量為 -1
        item.setQuantity(-1);

        shoppingCart.getLineItems().add(item);


        shoppingCartService.create(shoppingCart);
    }

    /**
     * Partition P2 (Invalid Zero: q = 0) — Boundary just below valid range
     * Purpose: Ensure zero quantity is not allowed for cart item creation.
     * Input: q = 0
     * Expected: shoppingCartService.create(...) throws an Exception; cart is not created.
     */
    @Test(expected = Exception.class)
    public void testAddToCart_ZeroQuantity() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setSku(product.getSku());

        // 設定數量為 0
        item.setQuantity(0);

        shoppingCart.getLineItems().add(item);

        shoppingCartService.create(shoppingCart);
    }

    /**
     * Partition P4 (Invalid OverStock: q > 10) — Boundary value
     * Purpose: Ensure quantities exceeding available inventory are rejected.
     * Input: q = 11 (stock = 10)
     * Expected: shoppingCartService.create(...) throws an Exception; cart is not created.
     */
    @Test(expected = Exception.class)
    public void testAddToCart_OverStock() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setSku(product.getSku());

        // 設定數量為 11
        item.setQuantity(11);

        shoppingCart.getLineItems().add(item);

        shoppingCartService.create(shoppingCart);
    }

    /**
     * Partition P1 (Valid Range: 1 <= q <= 10) — Lower boundary
     * Purpose: Verify the minimum valid quantity is accepted.
     * Input: q = 1
     * Expected: Cart creation succeeds; retrieved item quantity = 1.
     */
    @Test
    public void testAddToCart_MinimumValidQuantity() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setSku(product.getSku());
        item.setQuantity(1);  // 最小有效值

        shoppingCart.getLineItems().add(item);
        shoppingCartService.create(shoppingCart);

        ShoppingCart retrievedCart = shoppingCartService.getByCode(shoppingCart.getShoppingCartCode(), store);
        Assert.assertNotNull(retrievedCart);
        Assert.assertEquals(1, retrievedCart.getLineItems().iterator().next().getQuantity().intValue());
    }

    /**
     * Partition P1 (Valid Range: 1 <= q <= 10) — Upper boundary
     * Purpose: Verify the maximum valid quantity (equal to stock) is accepted.
     * Input: q = 10 (stock = 10)
     * Expected: Cart creation succeeds; retrieved item quantity = 10.
     */
    @Test
    public void testAddToCart_MaximumValidQuantity() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setSku(product.getSku());
        item.setQuantity(10);  // 最大有效值

        shoppingCart.getLineItems().add(item);
        shoppingCartService.create(shoppingCart);

        ShoppingCart retrievedCart = shoppingCartService.getByCode(shoppingCart.getShoppingCartCode(), store);
        Assert.assertNotNull(retrievedCart);
        Assert.assertEquals(10, retrievedCart.getLineItems().iterator().next().getQuantity().intValue());
    }

    /**
     * Partition P1 (Valid Range: 1 <= q <= 10) — Representative value
     * Purpose: Add another in-range representative to improve partition coverage.
     * Input: q = 3
     * Expected: Cart creation succeeds; retrieved item quantity = 3.
     */
    @Test
    public void testAddToCart_MidRangeQuantity() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setSku(product.getSku());
        item.setQuantity(3);

        shoppingCart.getLineItems().add(item);
        shoppingCartService.create(shoppingCart);

        ShoppingCart retrievedCart = shoppingCartService.getByCode(shoppingCart.getShoppingCartCode(), store);
        Assert.assertNotNull(retrievedCart);
        Assert.assertEquals(3, retrievedCart.getLineItems().iterator().next().getQuantity().intValue());
    }

    /**
     * Partition P3 (Invalid Negative: q < 0) — Extreme value
     * Purpose: Ensure the validation rejects extreme negative quantities as well.
     * Input: q = -100
     * Expected: shoppingCartService.create(...) throws an Exception; cart is not created.
     */
    @Test(expected = Exception.class)
    public void testAddToCart_ExtremeNegativeQuantity() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setSku(product.getSku());
        item.setQuantity(-100);  // 極端負數

        shoppingCart.getLineItems().add(item);
        shoppingCartService.create(shoppingCart);
    }

    /**
     * Partition P4 (Invalid OverStock: q > 10) — Extreme value
     * Purpose: Ensure the validation rejects very large quantities beyond stock.
     * Input: q = 100 (stock = 10)
     * Expected: shoppingCartService.create(...) throws an Exception; cart is not created.
     */
    @Test(expected = Exception.class)
    public void testAddToCart_ExtremeOverStock() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setSku(product.getSku());
        item.setQuantity(100);  // 遠超庫存

        shoppingCart.getLineItems().add(item);
        shoppingCartService.create(shoppingCart);
    }

    /**
     * Scenario S1 (Non-partition): Multiple line items in one cart
     * Purpose: Verify the cart can persist multiple ShoppingCartItem entries successfully
     *         when each item quantity is valid.
     * Inputs: item1 q = 2, item2 q = 3  (both in P1)
     * Expected: Cart creation succeeds; retrieved cart contains 2 line items.
     */
    @Test
    public void testAddToCart_MultipleItems() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        // 第一個商品條目：2個
        ShoppingCartItem item1 = new ShoppingCartItem(shoppingCart, product);
        item1.setSku(product.getSku());
        item1.setQuantity(2);
        shoppingCart.getLineItems().add(item1);

        // 第二個商品條目：3個
        ShoppingCartItem item2 = new ShoppingCartItem(shoppingCart, product);
        item2.setSku(product.getSku());
        item2.setQuantity(3);
        shoppingCart.getLineItems().add(item2);

        shoppingCartService.create(shoppingCart);

        ShoppingCart retrievedCart = shoppingCartService.getByCode(shoppingCart.getShoppingCartCode(), store);
        Assert.assertNotNull(retrievedCart);
        Assert.assertEquals(2, retrievedCart.getLineItems().size());
    }

    /**
     * Partition P1 (Valid Range: 1 <= q <= 10) — Near upper boundary
     * Purpose: Verify a near-maximum valid quantity is accepted (helps catch off-by-one).
     * Input: q = 9
     * Expected: Cart creation succeeds; retrieved item quantity = 9.
     */
    @Test
    public void testAddToCart_NearMaximumQuantity() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setSku(product.getSku());
        item.setQuantity(9);  // 接近最大值

        shoppingCart.getLineItems().add(item);
        shoppingCartService.create(shoppingCart);

        ShoppingCart retrievedCart = shoppingCartService.getByCode(shoppingCart.getShoppingCartCode(), store);
        Assert.assertNotNull(retrievedCart);
        Assert.assertEquals(9, retrievedCart.getLineItems().iterator().next().getQuantity().intValue());
    }
}