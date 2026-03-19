# Testable Design and Mocking Analysis Report

## Part 1: Testable Design (20 points)

### 1. Describing Testable Design Aspects and Goals

**Testable design** refers to code architecture and patterns that make it easy to write automated tests. The following aspects define good testable design:

#### 1.1 Five Key Aspects of Testable Design

| Aspect | Description | Benefit |
|--------|-------------|---------|
| **Dependency Injection (DI)** | Classes receive their dependencies through constructor, setter, or field injection rather than creating them internally | Allows tests to inject mock objects instead of real ones |
| **Single Responsibility Principle (SRP)** | Each class has one reason to change; minimal dependencies | Easier to test in isolation; fewer test cases needed |
| **Interface Contracts** | Use interfaces instead of concrete implementations | Can create test doubles (stubs, mocks) without depending on actual implementation |
| **Inversion of Control (IoC)** | Caller doesn't decide how to create objects; container does | Enables flexible object replacement for testing |
| **No Hidden Dependencies** | All dependencies explicitly declared, not hidden inside methods | Tests can see all inputs and outputs; no surprise external calls |

#### 1.2 Goals of Testable Design

1. **Isolation**: Test one component without running others
2. **Repeatability**: Tests produce same results every run
3. **Speed**: Tests execute quickly (no database, network calls)
4. **Control**: Tests control external dependencies (time, randomness, I/O)
5. **Clarity**: Test code clearly expresses intent and setup

---

### 2. Finding an Example of Stubbing

#### 2.1 Existing Stubbing Implementation

**File**: [sm-core/src/test/java/com/salesmanager/test/business/utils/DataUtilsTest.java](sm-core/src/test/java/com/salesmanager/test/business/utils/DataUtilsTest.java#L24-L28)

**Code Example**:
```java
@Test
public void testGetWeight_When_StoreUnit_LB_MeasurementUnit_LB(){
    // Stubbing: Create a mock MerchantStore and define its behavior
    MerchantStore store = mock(MerchantStore.class);
    when(store.getWeightunitcode()).thenReturn(MeasureUnit.LB.name());
    
    // Call the actual method being tested with the stubbed dependency
    double result = DataUtils.getWeight(100.789, store, MeasureUnit.LB.name());
    
    // Verify the result
    assertEquals(100.79, result, 0);
}
```

#### 2.2 How It Works and Why

**What is being stubbed**: The `MerchantStore` interface

**How**: Using Mockito's `mock()` and `when().thenReturn()` pattern
- `mock(MerchantStore.class)` creates a test double (stub)
- `when(store.getWeightunitcode()).thenReturn(MeasureUnit.LB.name())` defines that when this method is called, return a pre-defined value

**Why**:
- `MerchantStore` is a database entity that would require actual database setup to test
- By stubbing it, the test focuses only on the weight conversion logic in `DataUtils.getWeight()`
- The test runs in milliseconds without database overhead
- The test is deterministic: returns same value every run (not random database state)

**Stubbing vs. Real Object**:
- ❌ Without stub: Would need to create a real MerchantStore, persist it to database, create test database
- ✅ With stub: Create lightweight in-memory test double, control exactly what values it returns

---

### 3. Stubbing an Existing Method in a New Test

#### 3.1 (Example improvement we'll implement below - see Section 3.1.1)

We'll create a new test that demonstrates more advanced stubbing of the `MerchantStore` interface.

---

### 4. Bad Testable Design - Poor Implementation Example

#### 4.1 Problem Code Analysis

**File**: [sm-core/src/main/java/com/salesmanager/core/business/services/reference/loader/IntegrationModulesLoader.java](sm-core/src/main/java/com/salesmanager/core/business/services/reference/loader/IntegrationModulesLoader.java)

**The Problem Code**:
```java
@Component
public class IntegrationModulesLoader {
    
    // PROBLEM 1: Hard-coded ObjectMapper creation inside method
    public List<IntegrationModule> loadIntegrationModules(String jsonFilePath) throws Exception {
        List<IntegrationModule> modules = new ArrayList<IntegrationModule>();
        
        // ObjectMapper is created directly, not injected
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            // PROBLEM 2: File I/O with hardcoded classloader logic
            InputStream in = this.getClass()
                .getClassLoader()
                .getResourceAsStream(jsonFilePath);
            
            Map[] objects = mapper.readValue(in, Map[].class);
            // ... processing
            return modules;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
```

#### 4.2 Why This Design is Untestable

| Problem | Why It's Untestable | Impact |
|---------|-------------------|--------|
| **Hard-coded ObjectMapper** | Cannot replace with a mock that simulates errors or specific parsing behaviors | Difficult to test error handling paths |
| **Direct File System Access** | Test requires actual JSON files on classpath; can't test different file contents | Brittle tests; sensitive to file path changes |
| **No Dependency Injection** | All dependencies created internally; no way to inject test doubles | Must test real implementation, not logic |
| **Tight Coupling to Implementation** | Test must know the exact JSON file path and structure | Test is fragile; changes to JSON structure break tests |
| **Mixed Concerns** | Class does file I/O AND parsing AND object creation | Hard to unit test parsing separately from I/O |

**Testing Challenges**:
1. **Cannot test error handling**: What if ObjectMapper fails? Can't simulate this
2. **Cannot test different JSON formats**: Would need multiple test JSON files
3. **File might not exist in test environment**: Test will fail in CI/CD
4. **Hard to mock file system state**: Network-mounted files, permissions issues

---

### 5. Improved Testable Design

#### 5.1 Refactored Code with Better Testable Design

**New Design Principle**: Use dependency injection and interface contracts

```java
// Step 1: Create an interface to abstract file/stream loading
public interface IntegrationModuleSource {
    /**
     * Load raw JSON as string from a source.
     * This allows different implementations: file, database, network, or test mock
     */
    String loadRawJson(String identifier) throws ServiceException;
}

// Step 2: Create interface for parsing logic
public interface IntegrationModuleParser {
    /**
     * Parse a list of modules from JSON string.
     * Separated from I/O concerns.
     */
    List<IntegrationModule> parseModules(String jsonContent) throws ServiceException;
}

// Step 3: Refactor the loader with dependency injection
@Component
public class IntegrationModulesLoader {
    
    // Now dependencies are injected, can be replaced with test doubles
    private final IntegrationModuleSource source;
    private final IntegrationModuleParser parser;
    
    // Constructor injection - dependencies clear and required
    public IntegrationModulesLoader(
        IntegrationModuleSource source,
        IntegrationModuleParser parser) {
        this.source = source;
        this.parser = parser;
    }
    
    /**
     * Load and parse modules.
     * Now testable: can inject mock source and parser
     */
    public List<IntegrationModule> loadIntegrationModules(String jsonFilePath) 
            throws ServiceException {
        String jsonContent = source.loadRawJson(jsonFilePath);
        return parser.parseModules(jsonContent);
    }
}

// Step 4: Original file-based implementation (production)
@Component
public class ClasspathIntegrationModuleSource implements IntegrationModuleSource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(
        ClasspathIntegrationModuleSource.class);
    
    @Override
    public String loadRawJson(String jsonFilePath) throws ServiceException {
        try {
            InputStream in = this.getClass()
                .getClassLoader()
                .getResourceAsStream(jsonFilePath);
            
            if (in == null) {
                throw new ServiceException("File not found: " + jsonFilePath);
            }
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(in));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            return content.toString();
        } catch (Exception e) {
            throw new ServiceException("Failed to load JSON from " + jsonFilePath, e);
        }
    }
}

// Step 5: Parser implementation (separated concern)
@Component
public class ObjectMapperIntegrationModuleParser 
        implements IntegrationModuleParser {
    
    private final ObjectMapper mapper;
    
    public ObjectMapperIntegrationModuleParser() {
        this.mapper = new ObjectMapper();
    }
    
    @Override
    public List<IntegrationModule> parseModules(String jsonContent) 
            throws ServiceException {
        try {
            // Parse JSON to map array
            Map[] objects = mapper.readValue(jsonContent, Map[].class);
            List<IntegrationModule> modules = new ArrayList<>();
            
            for (Map object : objects) {
                modules.add(this.parseModule(object));
            }
            return modules;
        } catch (Exception e) {
            throw new ServiceException("Failed to parse modules from JSON", e);
        }
    }
    
    private IntegrationModule parseModule(Map<String, Object> object) {
        IntegrationModule module = new IntegrationModule();
        module.setModule((String) object.get("module"));
        module.setCode((String) object.get("code"));
        module.setImage((String) object.get("image"));
        // ... other fields
        return module;
    }
}
```

#### 5.2 Why This is Better Testable Design

| Aspect | Before | After |
|--------|--------|-------|
| **File I/O** | Hard-coded in method | Injected dependency (can be mocked) |
| **Parsing Logic** | Mixed with I/O | Separated interface (can test independently) |
| **Dependency Declaration** | Hidden inside method | Explicit in constructor |
| **Test Doubles** | Impossible | Easy to create test stubs |
| **Error Handling** | Can't test failures | Can mock failures easily |

---

### 6. Test Case for Improved Testable Code

**File**: Create new test file at `sm-core/src/test/java/com/salesmanager/test/business/loader/IntegrationModulesLoaderTest.java`

```java
package com.salesmanager.test.business.loader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.loader.IntegrationModulesLoader;
import com.salesmanager.core.model.system.IntegrationModule;

/**
 * Test case demonstrating improved testable design
 * Using dependency injection and mocking
 */
public class IntegrationModulesLoaderTest {
    
    /**
     * Stub implementation of IntegrationModuleSource for testing
     */
    static class StubIntegrationModuleSource implements IntegrationModuleSource {
        private String jsonToReturn;
        private boolean throwException;
        
        public StubIntegrationModuleSource(String jsonToReturn) {
            this.jsonToReturn = jsonToReturn;
            this.throwException = false;
        }
        
        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }
        
        @Override
        public String loadRawJson(String identifier) throws ServiceException {
            if (throwException) {
                throw new ServiceException("Stubbed exception: file not found");
            }
            return jsonToReturn;
        }
    }
    
    @Test
    public void testLoadModulesWithValidJson() throws Exception {
        // Arrange: Create stub that returns valid JSON
        String validJson = "[{" +
            "\"module\": \"payment\", " +
            "\"code\": \"paypal\", " +
            "\"image\": \"paypal.png\"" +
            "}]";
        
        IntegrationModuleSource stubSource = new StubIntegrationModuleSource(validJson);
        IntegrationModuleParser mockParser = mock(IntegrationModuleParser.class);
        
        // Setup mock parser to return modules
        List<IntegrationModule> expectedModules = new ArrayList<>();
        IntegrationModule module = new IntegrationModule();
        module.setCode("paypal");
        expectedModules.add(module);
        
        when(mockParser.parseModules(validJson)).thenReturn(expectedModules);
        
        // Act: Create loader with injected stub and mock
        IntegrationModulesLoader loader = new IntegrationModulesLoader(
            stubSource, mockParser);
        List<IntegrationModule> result = loader.loadIntegrationModules("data/modules.json");
        
        // Assert
        assertNotNull("Result should not be null", result);
        assertEquals("Should have 1 module", 1, result.size());
        assertEquals("Module code should be paypal", "paypal", result.get(0).getCode());
        
        // Verify the parser was called with the JSON content
        verify(mockParser).parseModules(validJson);
    }
    
    @Test
    public void testLoadModulesWithSourceException() throws Exception {
        // Arrange: Create stub that throws exception
        StubIntegrationModuleSource stubSource = new StubIntegrationModuleSource("");
        stubSource.setThrowException(true);
        
        IntegrationModuleParser mockParser = mock(IntegrationModuleParser.class);
        
        // Act & Assert
        IntegrationModulesLoader loader = new IntegrationModulesLoader(
            stubSource, mockParser);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            loader.loadIntegrationModules("nonexistent.json");
        });
        
        // Verify exception message
        assertTrue("Exception should mention file not found", 
            exception.getMessage().contains("file not found"));
    }
    
    @Test
    public void testLoadModulesWithParsingError() throws Exception {
        // Arrange: Valid source but parser fails
        String validJson = "[invalid json]";
        IntegrationModuleSource validSource = new StubIntegrationModuleSource(validJson);
        
        IntegrationModuleParser mockParser = mock(IntegrationModuleParser.class);
        when(mockParser.parseModules(validJson))
            .thenThrow(new ServiceException("Parse error"));
        
        // Act & Assert
        IntegrationModulesLoader loader = new IntegrationModulesLoader(
            validSource, mockParser);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            loader.loadIntegrationModules("data/modules.json");
        });
        
        assertTrue("Exception should mention parse error", 
            exception.getMessage().contains("Parse error"));
    }
    
    /**
     * Test showing how different JSON sources can be easily swapped
     * This demonstrates the value of dependency injection
     */
    @Test
    public void testLoaderWithDifferentSources() throws Exception {
        // Source 1: Returns modules
        IntegrationModuleSource source1 = new StubIntegrationModuleSource(
            "[{\"module\": \"payment\", \"code\": \"stripe\", \"image\": \"stripe.png\"}]");
        
        // Source 2: Returns empty list
        IntegrationModuleSource source2 = new StubIntegrationModuleSource("[]");
        
        // Source 3: Simulates database error
        StubIntegrationModuleSource source3 = new StubIntegrationModuleSource("");
        source3.setThrowException(true);
        
        IntegrationModuleParser mockParser = mock(IntegrationModuleParser.class);
        when(mockParser.parseModules(anyString()))
            .thenReturn(new ArrayList<>());
        
        // Each loader uses different source - would be different with old design
        IntegrationModulesLoader loader1 = new IntegrationModulesLoader(source1, mockParser);
        IntegrationModulesLoader loader2 = new IntegrationModulesLoader(source2, mockParser);
        IntegrationModulesLoader loader3 = new IntegrationModulesLoader(source3, mockParser);
        
        // With old design, would be impossible to test these scenarios
        assertThrows(ServiceException.class, () -> 
            loader3.loadIntegrationModules("modules.json"));
    }
}
```

---

## Part 2: Mocking (20 points)

### 1. Describing Mocking and Its Utility

#### 1.1 What is Mocking?

**Mocking** is creating a test double (a fake object) that:
- Has the same interface as a real object
- Records how it's called (call tracking)
- Allows defining expected behavior before test runs
- Allows verifying behavior after test runs

#### 1.2 Mocking vs. Stubbing

| Aspect | Stubbing | Mocking |
|--------|----------|---------|
| **Purpose** | Provide pre-defined return values | Track calls and verify behavior |
| **Interaction** | One-way (stub provides values) | Two-way (mock provides and verifies) |
| **Setup** | `when().thenReturn()` | `when().thenReturn()` + `verify()` |
| **Use Case** | Simple value substitution | Complex interaction verification |
| **Example** | Stub database to return fixed user | Mock payment gateway to verify correct amount was charged |

#### 1.3 Utility of Mocking

**Problem Without Mocking**:
```
Test needs to call PaymentService.charge(amount)
PaymentService calls real PaymentGateway API
Real API charges real money or rate-limits
Tests become slow, expensive, unreliable
```

**Solution With Mocking**:
```
Test creates mock PaymentGateway
Passes mock to PaymentService via Dependency Injection
Test calls PaymentService.charge(amount)
PaymentService calls mock (not real API)
Test verifies: Did service call gateway with correct amount?
No money charged, instant execution, reliable
```

---

### 2. Feature That Could Be Mocked

#### 2.1 Identified Feature: EmailService

**Current Situation**: The Shopizer project has an `EmailService` used throughout the codebase (visible in [AbstractSalesManagerCoreTestCase.java](sm-core/src/test/java/com/salesmanager/test/common/AbstractSalesManagerCoreTestCase.java#L113))

```java
@Inject
protected EmailService emailService;
```

**Problem**: 
- When testing order creation, customer registration, etc., actual emails are sent
- Tests are slow (SMTP network calls)
- Tests modify external state (real email inboxes)
- Hard to verify email content without parsing actual email servers
- CI/CD might not have email credentials

**How Mocking Would Help**:
1. **Isolate Logic**: Test order service without email service dependencies
2. **Verify Email Calls**: Ensure correct recipients, subject lines being sent
3. **Test Error Handling**: Simulate email service failures
4. **Speed**: No network calls, tests run in milliseconds
5. **CI/CD Friendly**: No credentials, no external dependencies

---

### 3. Test Case Using Mockito

**Create new test**: `sm-core/src/test/java/com/salesmanager/test/business/services/EmailServiceMockTest.java`

```java
package com.salesmanager.test.business.services;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.system.EmailService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;

/**
 * Mocking EmailService to verify email behavior without sending real emails
 * Demonstrates advanced Mockito features: verification, argument capturing, exceptions
 */
public class EmailServiceMockTest {
    
    /**
     * Example service that uses EmailService
     * (In real code, this would be OrderService, CustomerService, etc.)
     */
    static class OrderNotificationService {
        private EmailService emailService;
        
        public OrderNotificationService(EmailService emailService) {
            this.emailService = emailService;
        }
        
        /**
         * Send order confirmation email
         * This is the method we want to test without actually sending email
         */
        public void sendOrderConfirmation(String customerEmail, String orderId, 
                double totalAmount, MerchantStore store) throws ServiceException {
            
            // Validate inputs
            if (customerEmail == null || customerEmail.isEmpty()) {
                throw new ServiceException("Customer email is required");
            }
            if (totalAmount <= 0) {
                throw new ServiceException("Order amount must be positive");
            }
            
            // Build email content
            String subject = "Order Confirmation: " + orderId;
            String body = String.format(
                "Thank you for your order!\n" +
                "Order ID: %s\n" +
                "Total: $%.2f\n" +
                "Store: %s\n",
                orderId, totalAmount, store.getStorename());
            
            // Send email (this is what we'll mock)
            emailService.sendHtmlEmail(store, customerEmail, null, subject, body, null);
        }
        
        /**
         * Send email to admin when high-value order received
         */
        public void notifyAdminHighValueOrder(String adminEmail, String orderId, 
                double totalAmount) throws ServiceException {
            
            if (totalAmount > 10000) {
                String subject = "High-value order alert: " + orderId;
                String body = String.format(
                    "An order for $%.2f has been placed (Order ID: %s)\n" +
                    "This exceeds the $10,000 threshold.",
                    totalAmount, orderId);
                
                emailService.sendHtmlEmail(null, adminEmail, null, subject, body, null);
            }
        }
    }
    
    // Mocked dependencies
    @Mock
    private EmailService mockEmailService;
    
    @Mock
    private MerchantStore mockStore;
    
    private OrderNotificationService notificationService;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new OrderNotificationService(mockEmailService);
        
        // Setup mock store
        when(mockStore.getStorename()).thenReturn("Test Store");
    }
    
    /**
     * Test 1: Verify email is called with correct parameters
     * DEMONSTRATES: Argument verification and call counting
     */
    @Test
    public void testOrderConfirmationEmailIsSent() throws ServiceException {
        // Arrange
        String customerEmail = "customer@example.com";
        String orderId = "ORD-12345";
        double totalAmount = 99.99;
        
        // Act: Send order confirmation
        notificationService.sendOrderConfirmation(
            customerEmail, orderId, totalAmount, mockStore);
        
        // Assert (Verify): EmailService was called exactly once
        verify(mockEmailService, times(1)).sendHtmlEmail(
            any(MerchantStore.class),      // store
            eq(customerEmail),               // recipient email
            isNull(),                        // second recipient
            contains("ORD-12345"),          // subject contains order ID
            contains("99.99"),              // body contains amount
            isNull()                        // attachments
        );
    }
    
    /**
     * Test 2: Capture and verify email content details
     * DEMONSTRATES: ArgumentCaptor for detailed assertions on call arguments
     */
    @Test
    public void testOrderConfirmationEmailContentIsCorrect() throws ServiceException {
        // Arrange
        String customerEmail = "john@example.com";
        String orderId = "ORD-67890";
        double totalAmount = 299.50;
        
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        
        // Act
        notificationService.sendOrderConfirmation(
            customerEmail, orderId, totalAmount, mockStore);
        
        // Assert: Capture the arguments passed to sendHtmlEmail
        verify(mockEmailService).sendHtmlEmail(
            any(MerchantStore.class),
            eq(customerEmail),
            isNull(),
            subjectCaptor.capture(),
            bodyCaptor.capture(),
            isNull()
        );
        
        // Verify email subject and body content
        String subject = subjectCaptor.getValue();
        String body = bodyCaptor.getValue();
        
        assertTrue("Subject should contain order ID", 
            subject.contains("ORD-67890"));
        assertTrue("Subject should contain 'Confirmation'", 
            subject.contains("Confirmation"));
        assertTrue("Body should contain order ID", 
            body.contains("ORD-67890"));
        assertTrue("Body should contain amount", 
            body.contains("299.50"));
        assertTrue("Body should contain store name", 
            body.contains("Test Store"));
    }
    
    /**
     * Test 3: Verify email is NOT sent for invalid inputs
     * DEMONSTRATES: Exception handling and negative test cases
     */
    @Test
    public void testOrderConfirmationThrowsExceptionForEmptyEmail() {
        // Arrange: Empty customer email
        String customerEmail = "";
        
        // Act & Assert: Exception should be thrown
        assertThrows(ServiceException.class, () -> {
            notificationService.sendOrderConfirmation(
                customerEmail, "ORD-111", 50.0, mockStore);
        });
        
        // Verify: Email service should NOT be called
        verify(mockEmailService, never()).sendHtmlEmail(any(), any(), any(), any(), any(), any());
    }
    
    /**
     * Test 4: Verify email is NOT sent for zero/negative amounts
     * DEMONSTRATES: Input validation test
     */
    @Test
    public void testOrderConfirmationThrowsExceptionForNegativeAmount() {
        // Act & Assert: Negative amount should throw exception
        assertThrows(ServiceException.class, () -> {
            notificationService.sendOrderConfirmation(
                "customer@example.com", "ORD-222", -50.0, mockStore);
        });
        
        // Verify: Email service not called for invalid input
        verify(mockEmailService, never()).sendHtmlEmail(any(), any(), any(), any(), any(), any());
    }
    
    /**
     * Test 5: Simulate email service failure
     * DEMONSTRATES: Testing error handling with mock exceptions
     */
    @Test
    public void testOrderConfirmationHandlesEmailException() throws ServiceException {
        // Arrange: Mock email service to throw exception
        doThrow(new ServiceException("SMTP server unreachable"))
            .when(mockEmailService)
            .sendHtmlEmail(any(), any(), any(), any(), any(), any());
        
        // Act & Assert: Service should propagate email exception
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            notificationService.sendOrderConfirmation(
                "customer@example.com", "ORD-333", 75.0, mockStore);
        });
        
        // Verify exception contains email service error
        assertTrue("Exception should mention SMTP", 
            exception.getMessage().contains("SMTP"));
    }
    
    /**
     * Test 6: Verify high-value order notification logic
     * DEMONSTRATES: Conditional behavior verification
     */
    @Test
    public void testAdminNotificationSentForHighValueOrder() throws ServiceException {
        // Arrange
        String adminEmail = "admin@store.com";
        double highValue = 15000.0;
        
        // Act: Notify admin of high-value order
        notificationService.notifyAdminHighValueOrder(adminEmail, "ORD-999", highValue);
        
        // Assert: Admin notification should be sent
        verify(mockEmailService, times(1)).sendHtmlEmail(
            isNull(),
            eq(adminEmail),
            isNull(),
            contains("High-value order"),
            contains("15000"),
            isNull()
        );
    }
    
    /**
     * Test 7: Verify admin notification NOT sent for normal orders
     * DEMONSTRATES: Boundary testing
     */
    @Test
    public void testAdminNotificationNotSentForNormalOrder() throws ServiceException {
        // Arrange
        String adminEmail = "admin@store.com";
        double normalValue = 5000.0;  // Below 10000 threshold
        
        // Act: Notify admin (but shouldn't send for normal order)
        notificationService.notifyAdminHighValueOrder(adminEmail, "ORD-low", normalValue);
        
        // Assert: No email should be sent
        verify(mockEmailService, never()).sendHtmlEmail(any(), any(), any(), any(), any(), any());
    }
    
    /**
     * Test 8: Verify email sent for order exactly at high-value threshold
     * DEMONSTRATES: Boundary value analysis
     */
    @Test
    public void testAdminNotificationForOrderAtThreshold() throws ServiceException {
        // Arrange: Order amount = exactly at $10,000 threshold
        String adminEmail = "admin@store.com";
        double atThreshold = 10000.0;
        
        // Act
        notificationService.notifyAdminHighValueOrder(adminEmail, "ORD-threshold", atThreshold);
        
        // Assert: No email (threshold check is > 10000, not >= 10000)
        verify(mockEmailService, never()).sendHtmlEmail(any(), any(), any(), any(), any(), any());
        
        // But one cent more should send
        notificationService.notifyAdminHighValueOrder(adminEmail, "ORD-over", 10000.01);
        verify(mockEmailService, times(1)).sendHtmlEmail(any(), any(), any(), any(), any(), any());
    }
}
```

---

## Summary

This report demonstrates:

1. **Testable Design Principles**:
   - Identified 5 key aspects (DI, SRP, Interfaces, IoC, No Hidden Dependencies)
   - Found existing stubbing in `DataUtilsTest` using Mockito
   - Analyzed untestable code in `IntegrationModulesLoader`
   - Designed improved version with interfaces and injection
   - Created comprehensive test suite for improved code

2. **Mocking Techniques**:
   - Explained mocking vs. stubbing trade-offs
   - Identified `EmailService` as untestable dependency
   - Created 8 test cases demonstrating:
     - Basic mock verification
     - Argument capturing
     - Exception handling
     - Conditional logic testing
     - Boundary value analysis

Both principles are essential for writing maintainable, fast, reliable test suites in professional software development.
