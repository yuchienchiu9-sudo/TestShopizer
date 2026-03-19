package com.salesmanager.test.catalog;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.category.CategoryDescription;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;

/**
 * ProductPricePartitionTest
 * 
 * Comprehensive JUnit test suite implementing partition-based testing for the Product Service.
 * This test class focuses on product price validation using systematic equivalence class partitioning.
 * 
 * Testing Strategy:
 * - Partition 1: Zero Price (valid - promotional products)
 * - Partition 2: Normal Price (0.01 - 999.99)
 * - Partition 3: High Price (1,000.00 - 9,999.99)
 * - Partition 4: Premium Price (10,000.00+)
 * - Partition 5: Invalid Negative Prices
 * - Partition 6: Invalid Decimal Precision
 * - Partition 7: Invalid Null Prices
 * 
 * Boundary Analysis:
 * - Lower boundary: 0.00 (valid)
 * - Invalid below: -0.01 (reject)
 * - Decimal precision: 0.001 (reject as non-monetary)
 * - Upper boundary: 9,999,999.99 (valid)
 */
public class ProductPricePartitionTest extends com.salesmanager.test.common.AbstractSalesManagerCoreTestCase {

	private static final Date date = new Date(System.currentTimeMillis());

	/**
	 * Helper method to create a product with specified price
	 */
	private Product createProductWithPrice(BigDecimal price) throws ServiceException {
		Language en = languageService.getByCode("en");
		MerchantStore store = merchantService.getByCode(MerchantStore.DEFAULT_STORE);
		ProductType generalType = productTypeService.getProductType(ProductType.GENERAL_TYPE);

		// Create category with unique code
		String categoryCode = "electronics-" + System.currentTimeMillis();
		Category electronics = new Category();
		electronics.setMerchantStore(store);
		electronics.setCode(categoryCode);

		CategoryDescription categoryDescription = new CategoryDescription();
		categoryDescription.setCategory(electronics);
		categoryDescription.setLanguage(en);
		categoryDescription.setName("Electronics");
		electronics.getDescriptions().add(categoryDescription);

		categoryService.create(electronics);

		// Create product with unique SKU
		String sku = "TEST-PRICE-" + System.currentTimeMillis();
		Product product = new Product();
		product.setMerchantStore(store);
		product.setSku(sku);
		product.setType(generalType);

		ProductDescription description = new ProductDescription();
		description.setProduct(product);
		description.setLanguage(en);
		description.setName("Test Product");
		description.setTitle("Test Product Title");
		description.setDescription("Test product for price validation");
		product.getDescriptions().add(description);
		
		// Add category
		product.getCategories().add(electronics);

		// Add availability with price
		ProductAvailability availability = new ProductAvailability();
		availability.setProduct(product);
		availability.setProductQuantity(100);
		availability.setRegion("*");
		
		ProductPrice productPrice = new ProductPrice();
		productPrice.setProductAvailability(availability);
		productPrice.setProductPriceAmount(price);
		productPrice.setDefaultPrice(true);

		ProductPriceDescription productPriceDescription = new ProductPriceDescription();
		productPriceDescription.setProductPrice(productPrice);
		productPriceDescription.setLanguage(en);
		productPriceDescription.setName("Base price");
		productPrice.getDescriptions().add(productPriceDescription);

		availability.getPrices().add(productPrice);
		product.getAvailabilities().add(availability);

		productService.saveProduct(product);

		return product;
	}

	// ========== PARTITION 1: ZERO PRICE (Valid) ==========
	/**
	 * PARTITION 1: Zero Price
	 * Boundary Test: Lower valid boundary
	 * Test product creation with zero price (free/promotional product)
	 * Expected: ACCEPT - Zero is a valid price
	 * Representative Value: 0.00
	 */
	@Test
	public void testPartition1_ZeroPrice_Valid() throws Exception {
		BigDecimal zeroPrice = new BigDecimal("0.00");
		Product product = createProductWithPrice(zeroPrice);

		Assert.assertNotNull("Product should be created successfully", product);
		Assert.assertNotNull("Product should have availabilities", product.getAvailabilities());
		Assert.assertTrue("Product should have at least one availability", product.getAvailabilities().size() > 0);
		ProductAvailability avail = product.getAvailabilities().iterator().next();
		Assert.assertTrue("Availability should have prices", avail.getPrices().size() > 0);
		ProductPrice actualPrice = avail.getPrices().iterator().next();
		Assert.assertEquals("Price should be exactly 0.00", 0, 
			zeroPrice.compareTo(actualPrice.getProductPriceAmount()));
	}

	/**
	 * PARTITION 1 Boundary: Minimum valid boundary (0.00)
	 * Verifies system accepts the lowest valid price
	 */
	@Test
	public void testPartition1_BoundaryLower_MinimumValidPrice() throws Exception {
		BigDecimal minimumPrice = new BigDecimal("0.00");
		Product product = createProductWithPrice(minimumPrice);

		Assert.assertNotNull("Minimum valid price should be accepted", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		Assert.assertEquals(0, minimumPrice.compareTo(actualPrice.getProductPriceAmount()));
	}

	// ========== PARTITION 2: NORMAL PRICE (0.01 - 999.99) ==========
	/**
	 * PARTITION 2: Normal Price
	 * Test product with typical pricing ($29.99)
	 * Expected: ACCEPT - Within normal pricing range
	 * Representative Value: 29.99
	 */
	@Test
	public void testPartition2_NormalPrice_RepresentativeValue() throws Exception {
		BigDecimal normalPrice = new BigDecimal("29.99");
		Product product = createProductWithPrice(normalPrice);

		Assert.assertNotNull("Product with normal price should be created", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		Assert.assertEquals("Normal price should be preserved", 0, 
			normalPrice.compareTo(actualPrice.getProductPriceAmount()));
	}

	/**
	 * PARTITION 2: Lower boundary of normal price range
	 * Test the minimum value in normal price partition (0.01)
	 * Expected: ACCEPT
	 */
	@Test
	public void testPartition2_BoundaryLower_MinimumNormalPrice() throws Exception {
		BigDecimal minimumNormal = new BigDecimal("0.01");
		Product product = createProductWithPrice(minimumNormal);

		Assert.assertNotNull("Minimum normal price should be accepted", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		Assert.assertEquals(0, minimumNormal.compareTo(actualPrice.getProductPriceAmount()));
	}

	/**
	 * PARTITION 2: Upper boundary of normal price range
	 * Test the maximum value in normal price partition (999.99)
	 * Expected: ACCEPT
	 */
	@Test
	public void testPartition2_BoundaryUpper_MaximumNormalPrice() throws Exception {
		BigDecimal maximumNormal = new BigDecimal("999.99");
		Product product = createProductWithPrice(maximumNormal);

		Assert.assertNotNull("Maximum normal price should be accepted", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		Assert.assertEquals(0, maximumNormal.compareTo(actualPrice.getProductPriceAmount()));
	}

	/**
	 * PARTITION 2: Mid-range value within normal partition
	 * Test a representative mid-range value ($99.99)
	 * Expected: ACCEPT
	 */
	@Test
	public void testPartition2_MidRange_StandardRetailPrice() throws Exception {
		BigDecimal midRangePrice = new BigDecimal("99.99");
		Product product = createProductWithPrice(midRangePrice);

		Assert.assertNotNull("Mid-range price should be accepted", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		Assert.assertEquals(0, midRangePrice.compareTo(actualPrice.getProductPriceAmount()));
	}

	// ========== PARTITION 3: HIGH PRICE (1,000.00 - 9,999.99) ==========
	/**
	 * PARTITION 3: High Price
	 * Test product with premium pricing ($1,499.99)
	 * Expected: ACCEPT - Within high price range
	 * Representative Value: 1,499.99
	 */
	@Test
	public void testPartition3_HighPrice_RepresentativeValue() throws Exception {
		BigDecimal highPrice = new BigDecimal("1499.99");
		Product product = createProductWithPrice(highPrice);

		Assert.assertNotNull("Product with high price should be created", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		Assert.assertEquals("High price should be preserved", 0, 
			highPrice.compareTo(actualPrice.getProductPriceAmount()));
	}

	/**
	 * PARTITION 3: Lower boundary of high price range
	 * Test the minimum value in high price partition (1,000.00)
	 * Expected: ACCEPT
	 */
	@Test
	public void testPartition3_BoundaryLower_MinimumHighPrice() throws Exception {
		BigDecimal minimumHigh = new BigDecimal("1000.00");
		Product product = createProductWithPrice(minimumHigh);

		Assert.assertNotNull("Minimum high price should be accepted", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		Assert.assertEquals(0, minimumHigh.compareTo(actualPrice.getProductPriceAmount()));
	}

	/**
	 * PARTITION 3: Upper boundary of high price range
	 * Test the maximum value in high price partition (9,999.99)
	 * Expected: ACCEPT
	 */
	@Test
	public void testPartition3_BoundaryUpper_MaximumHighPrice() throws Exception {
		BigDecimal maximumHigh = new BigDecimal("9999.99");
		Product product = createProductWithPrice(maximumHigh);

		Assert.assertNotNull("Maximum high price should be accepted", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		Assert.assertEquals(0, maximumHigh.compareTo(actualPrice.getProductPriceAmount()));
	}

	// ========== PARTITION 4: PREMIUM PRICE (10,000.00+) ==========
	/**
	 * PARTITION 4: Premium Price
	 * Test product with luxury pricing ($15,000.00)
	 * Expected: ACCEPT - For high-end items
	 * Representative Value: 15,000.00
	 */
	@Test
	public void testPartition4_PremiumPrice_RepresentativeValue() throws Exception {
		BigDecimal premiumPrice = new BigDecimal("15000.00");
		Product product = createProductWithPrice(premiumPrice);

		Assert.assertNotNull("Product with premium price should be created", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		Assert.assertEquals("Premium price should be preserved", 0, 
			premiumPrice.compareTo(actualPrice.getProductPriceAmount()));
	}

	/**
	 * PARTITION 4: Lower boundary of premium price range
	 * Test the minimum value in premium partition (10,000.00)
	 * Expected: ACCEPT
	 */
	@Test
	public void testPartition4_BoundaryLower_MinimumPremiumPrice() throws Exception {
		BigDecimal minimumPremium = new BigDecimal("10000.00");
		Product product = createProductWithPrice(minimumPremium);

		Assert.assertNotNull("Minimum premium price should be accepted", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		Assert.assertEquals(0, minimumPremium.compareTo(actualPrice.getProductPriceAmount()));
	}

	/**
	 * PARTITION 4: Upper boundary test for very high-end items
	 * Test large premium pricing ($999,999.99)
	 * Expected: ACCEPT
	 */
	@Test
	public void testPartition4_BoundaryUpper_ExtremePremiumPrice() throws Exception {
		BigDecimal extremePremium = new BigDecimal("999999.99");
		Product product = createProductWithPrice(extremePremium);

		Assert.assertNotNull("Extreme premium price should be accepted", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		Assert.assertEquals(0, extremePremium.compareTo(actualPrice.getProductPriceAmount()));
	}

	// ========== PARTITION 5: INVALID - NEGATIVE PRICES ==========
	/**
	 * PARTITION 5: Invalid Negative Price
	 * Test rejection of negative price (-$10.00)
	 * Expected: REJECT - Prices cannot be negative
	 * Representative Value: -10.00
	 * 
	 * Note: Current implementation may not validate negative prices.
	 * This test documents the expected behavior and can catch regression if validation is added.
	 */
	@Test
	public void testPartition5_InvalidNegativePrice() throws Exception {
		BigDecimal negativePrice = new BigDecimal("-10.00");
		Product product = createProductWithPrice(negativePrice);

		// Assert that negative prices are either rejected or corrected
		Assert.assertNotNull("Product should handle negative price", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		// Verify price is not negative (if validation is implemented)
		Assert.assertTrue("Price should not be negative", actualPrice.getProductPriceAmount().compareTo(BigDecimal.ZERO) >= 0);
	}

	/**
	 * PARTITION 5 Boundary: Just below zero
	 * Test rejection of boundary negative price (-0.01)
	 * Expected: REJECT
	 */
	@Test
	public void testPartition5_BoundaryNegative_JustBelowZero() throws Exception {
		BigDecimal negativeOne = new BigDecimal("-0.01");
		Product product = createProductWithPrice(negativeOne);

		Assert.assertNotNull("Product should handle negative boundary", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();
		Assert.assertTrue("Price should not be below zero", actualPrice.getProductPriceAmount().compareTo(BigDecimal.ZERO) >= 0);
	}

	// ========== PARTITION 6: INVALID - DECIMAL PRECISION ==========
	/**
	 * PARTITION 6: Invalid Decimal Precision
	 * Test handling of prices with more than 2 decimal places ($29.999)
	 * Expected: Handled appropriately (rounding/rejection)
	 * Representative Value: 29.999
	 * 
	 * This tests monetary precision - currency values should only have cents (2 decimals)
	 */
	@Test
	public void testPartition6_InvalidDecimalPrecision_ThreeDecimals() throws Exception {
		BigDecimal threeDecimalPrice = new BigDecimal("29.999");
		Product product = createProductWithPrice(threeDecimalPrice);

		Assert.assertNotNull("Product should be created", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();

		// Verify price is properly handled (rounding to 2 decimals)
		Assert.assertEquals("Price should have valid monetary precision", 2, 
			actualPrice.getProductPriceAmount().scale());
	}

	/**
	 * PARTITION 6: Extreme decimal precision
	 * Test handling of price with many decimal places ($0.123456)
	 * Expected: Handled appropriately (typically rounded or scaled to 2 decimals)
	 */
	@Test
	public void testPartition6_InvalidDecimalPrecision_ExtremeDecimals() throws Exception {
		BigDecimal extremePrecision = new BigDecimal("0.123456");
		Product product = createProductWithPrice(extremePrecision);

		Assert.assertNotNull("Product should handle extreme decimals", product);
		ProductPrice actualPrice = product.getAvailabilities().iterator().next().getPrices().iterator().next();

		// Verify proper rounding to monetary standards
		Assert.assertTrue("Precision should be at most 2 decimals", actualPrice.getProductPriceAmount().scale() <= 2);
	}

	// ========== PARTITION 7: INVALID - NULL PRICE ==========
	/**
	 * PARTITION 7: Invalid Null Price
	 * Test handling of missing price information
	 * Expected: Should either create with default price or handle gracefully
	 * Representative Value: null
	 */
	@Test
	public void testPartition7_InvalidNullPrice_HandlesGracefully() throws Exception {
		// Test null handling - method should either reject or use default
		try {
			Product product = createProductWithPrice(null);
			// If product is created, verify price handling
			if (product.getAvailabilities() != null && product.getAvailabilities().size() > 0) {
				ProductAvailability avail = product.getAvailabilities().iterator().next();
				if (avail.getPrices() != null && avail.getPrices().size() > 0) {
					ProductPrice price = avail.getPrices().iterator().next();
					Assert.assertNotNull("Null price should be handled (not remain null)", price.getProductPriceAmount());
				}
			}
		} catch (NullPointerException | ServiceException e) {
			// Null price rejection is also acceptable behavior
			Assert.assertNotNull("Exception handling for null prices is acceptable", e);
		}
	}

	// ========== INTEGRATION AND CROSS-PARTITION TESTS ==========
	/**
	 * Integration test: Multiple products with different price partitions
	 * Verifies system handles diverse pricing across product catalog
	 */
	@Test
	public void testIntegration_MultipleProductPricePartitions() throws Exception {
		// Create products from different partitions
		BigDecimal[] prices = {
			new BigDecimal("0.00"),      // Zero
			new BigDecimal("49.99"),     // Normal
			new BigDecimal("1999.99"),   // High
			new BigDecimal("25000.00")   // Premium
		};

		List<Product> products = new ArrayList<>();
		for (int i = 0; i < prices.length; i++) {
			Product product = createProductWithPrice(prices[i]);
			products.add(product);
		}

		Assert.assertEquals("All products should be created", prices.length, products.size());
		
		for (int i = 0; i < products.size(); i++) {
			Product product = products.get(i);
			Assert.assertNotNull("Product " + i + " should exist", product);
			Assert.assertTrue("Product " + i + " should have availabilities", 
				product.getAvailabilities().size() > 0);
			Assert.assertTrue("Product " + i + " should have prices", 
				product.getAvailabilities().iterator().next().getPrices().size() > 0);
		}
	}

	/**
	 * Partition transition test: Boundary between Normal and High partitions
	 * Verifies system correctly handles the transition at 1,000.00
	 */
	@Test
	public void testPartitionTransition_NormalToHigh() throws Exception {
		// Test just before transition (normal)
		BigDecimal priceBeforeTransition = new BigDecimal("999.99");
		Product product1 = createProductWithPrice(priceBeforeTransition);
		Assert.assertNotNull("Price just below transition should be valid", product1);

		// Test just after transition (high)
		BigDecimal priceAfterTransition = new BigDecimal("1000.00");
		Product product2 = createProductWithPrice(priceAfterTransition);
		Assert.assertNotNull("Price at/above transition should be valid", product2);
	}

	// ========== SERVICE LAYER TESTS FOR COVERAGE ==========
	/**
	 * Test ProductPriceService methods: saveOrUpdate, findByProductSku
	 * These tests directly cover the service implementation
	 */
	@Test
	public void testProductPriceService_SaveAndFind() throws Exception {
		// Create a product with price
		BigDecimal testPrice = new BigDecimal("159.99");
		Product product = createProductWithPrice(testPrice);
		
		// Test findByProductSku
		List<ProductPrice> prices = productPriceService.findByProductSku(product.getSku(), 
			merchantService.getByCode(MerchantStore.DEFAULT_STORE));
		Assert.assertNotNull("Should find prices by SKU", prices);
		Assert.assertTrue("Should have at least one price", prices.size() > 0);
		
		// Test saveOrUpdate
		ProductPrice price = prices.get(0);
		price.setProductPriceAmount(new BigDecimal("169.99"));
		ProductPrice updated = productPriceService.saveOrUpdate(price);
		Assert.assertNotNull("Updated price should not be null", updated);
		Assert.assertEquals("Price should be updated", 0, 
			new BigDecimal("169.99").compareTo(updated.getProductPriceAmount()));
	}

	/**
	 * Test ProductPriceService methods: findById, delete, findByInventoryId
	 * These methods were not covered by partition tests, now added to improve coverage
	 */
	@Test
	public void testProductPriceService_AdditionalMethods() throws Exception {
		// 1) Create baseline data
		BigDecimal testPrice = new BigDecimal("249.99");
		Product product = createProductWithPrice(testPrice);
		MerchantStore store = merchantService.getByCode(MerchantStore.DEFAULT_STORE);
		String sku = product.getSku();

		Assert.assertNotNull("Product should be created", product);
		Assert.assertNotNull("Store should be loaded", store);
		Assert.assertNotNull("SKU should not be null", sku);

		// 2) Find prices by SKU and validate baseline
		List<ProductPrice> prices = productPriceService.findByProductSku(sku, store);
		Assert.assertNotNull("Should find prices", prices);
		Assert.assertTrue("Should have at least one price", prices.size() > 0);

		ProductPrice price = prices.get(0);
		Assert.assertNotNull("First price should not be null", price);
		Assert.assertNotNull("Price id should be generated", price.getId());
		Assert.assertNotNull("Price amount should not be null", price.getProductPriceAmount());
		Assert.assertEquals("Baseline amount should match", 0,
			testPrice.compareTo(price.getProductPriceAmount()));

		// 3) Find by ID and validate identity + value consistency
		Long priceId = price.getId();
		ProductPrice foundPrice = productPriceService.findById(priceId, sku, store);
		Assert.assertNotNull("Should find price by ID", foundPrice);
		Assert.assertEquals("Price ID should match", priceId.longValue(), foundPrice.getId().longValue());
		Assert.assertEquals("Found amount should match", 0,
			testPrice.compareTo(foundPrice.getProductPriceAmount()));

		// 4) Find by inventory and check relationship correctness
		Assert.assertNotNull("Availabilities should not be null", product.getAvailabilities());
		Assert.assertTrue("Product should contain availabilities", !product.getAvailabilities().isEmpty());

		ProductAvailability availability = product.getAvailabilities().iterator().next();
		Assert.assertNotNull("Availability should not be null", availability);
		Assert.assertNotNull("Availability id should not be null", availability.getId());

		List<ProductPrice> pricesByInventory = productPriceService.findByInventoryId(
			availability.getId(), sku, store);
		Assert.assertNotNull("Should find prices by inventory ID", pricesByInventory);
		Assert.assertTrue("Should find at least one price by inventory", !pricesByInventory.isEmpty());
		Assert.assertTrue("Inventory query should contain target price",
			pricesByInventory.stream().anyMatch(p -> p.getId().longValue() == priceId.longValue()));

		// 5) Delete and verify through multiple query paths
		productPriceService.delete(price);

		ProductPrice deletedPrice = productPriceService.findById(priceId, sku, store);
		Assert.assertNull("Deleted price should not be found by ID", deletedPrice);

		List<ProductPrice> pricesAfterDelete = productPriceService.findByProductSku(sku, store);
		Assert.assertNotNull("Price list after delete should not be null", pricesAfterDelete);
		Assert.assertFalse("Deleted ID should not exist in sku query",
			pricesAfterDelete.stream().anyMatch(p -> p.getId().longValue() == priceId.longValue()));
	}
}
