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
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionType;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.shoppingcart.ShoppingCartItem;

/**
 * Project 1: Partition Testing for Shopping Cart Quantity
 * 1. Valid (1-10)
 * 2. Invalid (Negative)
 * 3. Invalid (Zero)
 * 4. Invalid (Over Stock > 10)
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
     * Partition 1: 有效區間 (Valid)
     * 測試：加入 5 個商品
     * 預期：成功
     */
    @Test
    public void testAddToCart_ValidQuantity() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setSku(product.getSku());

        // 設定數量為 5 (在庫存 10 以內)
        item.setQuantity(5);

        shoppingCart.getLineItems().add(item);
        shoppingCartService.create(shoppingCart);

        // 驗證是否真的存進去了
        ShoppingCart retrievedCart = shoppingCartService.getByCode(shoppingCart.getShoppingCartCode(), store);
        Assert.assertNotNull(retrievedCart);
        Assert.assertEquals(5, retrievedCart.getLineItems().iterator().next().getQuantity().intValue());

        System.out.println("Partition 1 (Valid) Passed: succeed to add five produces");
    }

    /**
     * Partition 2: 無效區間 - 負數 (Invalid Negative)
     * 測試：加入 -1 個商品
     * 預期：應該要報錯 (Exception)
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

        // 如果這裡沒報錯，測試就會失敗 (代表系統竟然允許負數)
        shoppingCartService.create(shoppingCart);
    }

    /**
     * Partition 3: 無效區間 - 零 (Invalid Zero)
     * 測試：加入 0 個商品
     * 預期：應該要報錯 (Exception)
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
     * Partition 4: 無效區間 - 超過庫存 (Invalid OverStock)
     * 測試：加入 11 個商品 (庫存只有 10)
     * 預期：應該要報錯 (Exception)
     */
    @Test(expected = Exception.class)
    public void testAddToCart_OverStock() throws Exception {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setMerchantStore(store);
        shoppingCart.setShoppingCartCode(UUID.randomUUID().toString());

        ShoppingCartItem item = new ShoppingCartItem(shoppingCart, product);
        item.setSku(product.getSku());

        // 設定數量為 11 (超過庫存 10)
        item.setQuantity(11);

        shoppingCart.getLineItems().add(item);

        shoppingCartService.create(shoppingCart);
    }
}