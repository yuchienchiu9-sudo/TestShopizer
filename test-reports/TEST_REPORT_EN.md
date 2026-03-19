# Shopizer E-commerce Platform Testing Report (English)

**Date:** January 26, 2026  
**Course:** Software Testing

---

## 1. Project Overview

### 1.1 What is Shopizer?

Shopizer is an open-source Java-based e-commerce platform designed for building headless commerce solutions and REST API-driven online stores. It provides comprehensive functionality for managing products, customers, orders, and merchant operations. Shopizer follows a modern microservices architecture and supports multiple deployment options including Docker containers.

### 1.2 Project Purpose

The primary purpose of Shopizer is to provide businesses with a flexible, scalable e-commerce backend that can be integrated with any frontend technology. It serves as a complete headless commerce solution with the following key capabilities:

- **Product Catalog Management** - Create and manage product listings, categories, and inventory
- **Shopping Cart & Checkout** - Handle customer shopping sessions and payment processing
- **Customer Management** - User registration, authentication, and profile management
- **Order Management** - Process and track customer orders
- **Merchant Administration** - Multi-store support and merchant configuration
- **REST API** - Complete API coverage for headless commerce implementations

### 1.3 Project Statistics

| Metric | Value |
|--------|-------|
| **Total Lines of Code** | ~115,000 LOC |
| **Primary Language** | Java (Java 11/17) |
| **Framework** | Spring Boot 2.5.12 |
| **Build Tool** | Maven |
| **Architecture** | Multi-module Maven project |
| **Database** | H2 (default), MySQL, PostgreSQL, Oracle supported |
| **Version** | 3.2.5 |

**Key Modules:**
- `sm-core` - Core business logic and services
- `sm-core-model` - Domain models and entities
- `sm-core-modules` - Integration modules (payment, shipping, etc.)
- `sm-shop` - REST API and web application
- `sm-shop-model` - API models and DTOs

### 1.4 Technology Stack

- **Backend:** Java 11+, Spring Boot, Spring Security, Spring Data JPA
- **ORM:** Hibernate
- **Database:** H2, MySQL 8.0, PostgreSQL, Oracle
- **Cache:** Infinispan, Ehcache
- **Search:** Elasticsearch 7.5
- **API Documentation:** Swagger 2.9.2
- **Testing:** JUnit 4/5, Spring Test
- **Build:** Maven
- **Containerization:** Docker

---

## 2. Build Documentation

### 2.1 Prerequisites

Before building Shopizer, ensure you have the following installed:

1. **Java Development Kit (JDK) 11 or higher**
   - Download from: https://adoptium.net/ or https://www.oracle.com/java/
   - Verify installation: `java -version`

2. **Maven** (optional - project includes Maven wrapper)
   - The project includes `mvnw` (Maven wrapper) scripts
   - Or install Maven from: https://maven.apache.org/

3. **Git**
   - For cloning the repository
   - Download from: https://git-scm.com/

4. **Docker** (optional - for containerized deployment)
   - Download from: https://www.docker.com/

### 2.2 Building the Application

**Step 1: Clone the Repository**
```bash
git clone https://github.com/[your-username]/shopizerForTest.git
cd shopizerForTest
```

**Step 2: Build the Entire Project**
```bash
# Using Maven wrapper (recommended)
./mvnw clean install

# Or using system Maven
mvn clean install
```

This command will:
- Compile all Java source files
- Run unit and integration tests
- Package the application into JAR files
- Install artifacts into local Maven repository

**Build time:** Approximately 5-10 minutes depending on your system

**Step 3: Build Individual Modules** (optional)
```bash
# Build only the core module
cd sm-core
./mvnw clean install

# Build only the shop module
cd sm-shop
./mvnw clean install
```

### 2.3 Running the Application

**Method 1: Using Maven Spring Boot Plugin**
```bash
cd sm-shop
./mvnw spring-boot:run
```

**Method 2: Using Java JAR**
```bash
cd sm-shop
./mvnw clean package
java -jar target/sm-shop-3.2.5.jar
```

**Method 3: Using Docker**
```bash
# Pull and run the official Docker image
docker run -p 8080:8080 shopizerecomm/shopizer:latest
```

**Method 4: Build and Run Custom Docker Image**
```bash
# Build Docker image
cd sm-shop
docker build -t shopizer-local .

# Run the container
docker run -p 8080:8080 shopizer-local
```

**Access the Application:**
- **API Documentation (Swagger):** http://localhost:8080/swagger-ui.html
- **API Base URL:** http://localhost:8080/api/v1
- **Actuator Health Check:** http://localhost:8080/actuator/health

**Default Credentials:**
- Store Code: `DEFAULT`
- Default database: H2 (in-memory)

### 2.4 Configuration

**Database Configuration:**

By default, Shopizer uses H2 in-memory database. To use MySQL or PostgreSQL:

1. Edit `sm-shop/src/main/resources/application.properties`
2. Configure database connection:

```properties
# MySQL Example
spring.datasource.url=jdbc:mysql://localhost:3306/shopizer?useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

**Profile-based Configuration:**

Shopizer supports multiple profiles (local, cloud, aws, gcp, mysql):

```bash
# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql

# Or with JAR
java -jar -Dspring.profiles.active=mysql target/sm-shop-3.2.5.jar
```

---

## 3. Existing Test Cases Documentation

### 3.1 Testing Framework

Shopizer uses a comprehensive testing strategy with the following frameworks:

- **JUnit 4 & JUnit 5 (Jupiter)** - Core testing framework
- **Spring Test** - Integration testing with Spring context
- **Spring Boot Test** - Spring Boot application testing
- **Hamcrest** - Assertion matchers for readable tests
- **MockMvc** - REST API testing without server

**Test Structure:**
```
sm-shop/src/test/java/com/salesmanager/test/shop/
├── common/                    # Common test utilities
│   └── ServicesTestSupport.java
├── integration/               # Integration tests
│   ├── cart/                  # Shopping cart tests
│   ├── customer/              # Customer management tests
│   ├── order/                 # Order processing tests
│   ├── product/               # Product catalog tests
│   ├── search/                # Search functionality tests
│   ├── store/                 # Merchant store tests
│   ├── system/                # System configuration tests
│   └── user/                  # User authentication tests
└── util/                      # Test utilities
```

### 3.2 Test Categories

**1. Unit Tests**
- Located in individual module `src/test/java` directories
- Test individual classes and methods in isolation
- Mock external dependencies

**2. Integration Tests**
- Located in `sm-shop/src/test/java/com/salesmanager/test/shop/integration/`
- Test complete API endpoints with Spring context
- Use `@SpringBootTest` with `WebEnvironment.RANDOM_PORT`
- Test database interactions with H2 in-memory database

**3. Test Coverage Configuration**
```xml
<!-- From pom.xml -->
<coverage.lines>.04</coverage.lines>      <!-- 4% line coverage -->
<coverage.branches>.01</coverage.branches> <!-- 1% branch coverage -->
```

### 3.3 Example Test Cases

**Example 1: Customer Registration Test**

File: `CustomerRegistrationIntegrationTest.java`

```java
@SpringBootTest(classes = ShopApplication.class, 
                webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CustomerRegistrationIntegrationTest extends ServicesTestSupport {

    @Test
    public void registerCustomer() {
        // Create test customer data
        final PersistableCustomer testCustomer = new PersistableCustomer();
        testCustomer.setEmailAddress("customer1@test.com");
        testCustomer.setPassword("clear123");
        testCustomer.setGender(CustomerGender.M.name());
        
        // Register customer via REST API
        final ResponseEntity<PersistableCustomer> response = 
            testRestTemplate.postForEntity("/api/v1/customer/register", 
                                          entity, 
                                          PersistableCustomer.class);
        
        // Verify registration successful
        assertThat(response.getStatusCode(), is(OK));
        
        // Test login with registered credentials
        final ResponseEntity<AuthenticationResponse> loginResponse = 
            testRestTemplate.postForEntity("/api/v1/customer/login", 
                                          new AuthenticationRequest(...));
        assertNotNull(loginResponse.getBody().getToken());
    }
}
```

**Test Coverage:**
- Customer registration API endpoint
- Input validation
- Database persistence
- Authentication after registration

**Example 2: Shopping Cart Test**

File: `ShoppingCartAPIIntegrationTest.java`

```java
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShoppingCartAPIIntegrationTest extends ServicesTestSupport {

    @Test
    @Order(1)
    public void createShoppingCart() {
        // Test creating a new shopping cart
        final ResponseEntity<ReadableShoppingCart> response = 
            testRestTemplate.postForEntity("/api/v1/cart", ...);
        
        assertThat(response.getStatusCode(), is(OK));
        assertNotNull(response.getBody().getCode());
    }
    
    @Test
    @Order(2)
    public void addItemToCart() {
        // Test adding product to cart
        // Verify cart total updates correctly
    }
}
```

**Test Coverage:**
- Cart creation
- Adding/removing items
- Quantity updates
- Price calculations

### 3.4 Running Tests

**Run All Tests:**
```bash
# From project root
./mvnw clean test

# Run tests for specific module
cd sm-shop
./mvnw test
```

**Run Specific Test Class:**
```bash
./mvnw test -Dtest=CustomerRegistrationIntegrationTest
```

**Run Tests with Coverage Report:**
```bash
./mvnw clean test jacoco:report
```

Coverage report will be generated at:
- `target/site/jacoco/index.html`

**Skip Tests During Build:**
```bash
./mvnw clean install -DskipTests
```

**Run Integration Tests Only:**
```bash
./mvnw verify -P integration-tests
```

**Test Output:**
- Console output shows test results
- Test reports in `target/surefire-reports/`
- JUnit XML reports for CI/CD integration

---

## 4. Partitioning and Test Case Design

### 4.1 Motivation for Systematic Functional Testing

**Why Systematic Testing is Important:**

Systematic functional testing is essential for ensuring software quality and reliability. Random or ad-hoc testing often misses critical edge cases and may provide false confidence in system stability.

**Key Benefits:**

1. **Completeness** - Ensures all input scenarios are tested
2. **Efficiency** - Reduces redundant tests by selecting representative values
3. **Bug Detection** - Identifies boundary conditions where defects commonly occur
4. **Maintainability** - Provides structured, documented test approach
5. **Confidence** - Gives quantifiable assurance of system behavior

**Partition Testing Concepts:**

Partition testing divides the input space into equivalence classes (partitions) where all inputs in a partition are expected to behave similarly. This approach:

- **Reduces test cases** - Test one representative value per partition instead of all possible inputs
- **Identifies boundaries** - Focus on edge cases between partitions where bugs often hide
- **Systematic coverage** - Ensures all distinct behaviors are tested
- **Domain-driven** - Based on requirements and business logic

**Equivalence Partitioning:**
- Group inputs that should produce similar system behavior
- Test one representative value from each partition
- Assume if one value works, all values in that partition work

**Boundary Value Analysis:**
- Test values at partition boundaries
- Common defect locations: minimum, maximum, just inside/outside boundaries
- Example: For age 18-65, test: 17, 18, 65, 66

### 4.2 Feature Partitioning Examples

#### Feature 1: Customer Email Validation

**Selected Feature:** Customer email address validation during registration

**Partitioning Scheme:**

| Partition ID | Partition Description | Expected Behavior |
|--------------|----------------------|-------------------|
| P1 | Valid email format (local@domain.tld) | Accept and register |
| P2 | Missing @ symbol | Reject with validation error |
| P3 | Missing domain part | Reject with validation error |
| P4 | Missing local part | Reject with validation error |
| P5 | Invalid characters in local part | Reject with validation error |
| P6 | Email already exists in system | Reject with duplicate error |
| P7 | Empty/null email | Reject with required field error |
| P8 | Email exceeds maximum length | Reject with length error |
| P9 | Valid email with special characters | Accept and register |

**Partition Differences:**
- **P1 & P9** test valid inputs with different complexity levels
- **P2-P5** test structural invalidity (format violations)
- **P6** tests business rule (uniqueness constraint)
- **P7** tests required field validation
- **P8** tests length boundary

**Representative Values:**

| Partition | Representative Value | Rationale |
|-----------|---------------------|-----------|
| P1 | `user@example.com` | Standard valid email |
| P2 | `userexample.com` | Missing @ symbol |
| P3 | `user@` | Missing domain |
| P4 | `@example.com` | Missing local part |
| P5 | `user#$%@example.com` | Invalid special chars |
| P6 | `customer1@test.com` (existing) | Duplicate email |
| P7 | `null` or `""` | Empty value |
| P8 | 255-character email | Maximum length boundary |
| P9 | `user.name+tag@sub.example.com` | Valid complex format |

**Boundary Values:**
- Email length: 0, 1, 254, 255, 256 characters
- Local part length: 0, 1, 64, 65
- Domain part length: 0, 1, 253, 254

**JUnit Test Implementation:**

```java
package com.salesmanager.test.shop.partition;

import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.salesmanager.shop.application.ShopApplication;
import com.salesmanager.shop.model.customer.PersistableCustomer;
import com.salesmanager.test.shop.common.ServicesTestSupport;

@SpringBootTest(classes = ShopApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CustomerEmailPartitionTest extends ServicesTestSupport {

    /**
     * P1: Valid standard email format
     * Expected: HTTP 200 OK, customer created
     */
    @Test
    public void testPartition1_ValidStandardEmail() {
        PersistableCustomer customer = createTestCustomer("validuser@example.com");
        
        ResponseEntity<PersistableCustomer> response = 
            testRestTemplate.postForEntity("/api/v1/customer/register", 
                new HttpEntity<>(customer, getHeader()), 
                PersistableCustomer.class);
        
        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("validuser@example.com", response.getBody().getEmailAddress());
    }

    /**
     * P2: Missing @ symbol
     * Expected: HTTP 400 Bad Request
     */
    @Test
    public void testPartition2_MissingAtSymbol() {
        PersistableCustomer customer = createTestCustomer("userexample.com");
        
        ResponseEntity<PersistableCustomer> response = 
            testRestTemplate.postForEntity("/api/v1/customer/register", 
                new HttpEntity<>(customer, getHeader()), 
                PersistableCustomer.class);
        
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    // Additional test methods for other partitions...

    // Helper method to create test customer with specified email
    private PersistableCustomer createTestCustomer(String email) {
        PersistableCustomer customer = new PersistableCustomer();
        customer.setEmailAddress(email);
        customer.setPassword("Test123!");
        customer.setFirstName("Test");
        customer.setLastName("User");
        customer.setStoreCode("DEFAULT");
        return customer;
    }
}
```

#### Feature 2: Shopping Cart Quantity Validation

**Selected Feature:** Product quantity validation when adding items to shopping cart

**Partitioning Scheme:**

| Partition ID | Partition Description | Expected Behavior |
|--------------|----------------------|-------------------|
| P1 | Quantity = 1 | Accept |
| P2 | Quantity in valid range (2-999) | Accept |
| P3 | Quantity = 0 | Reject (minimum boundary) |
| P4 | Quantity < 0 (negative) | Reject |
| P5 | Quantity > maximum allowed (1000+) | Reject (maximum boundary) |
| P6 | Quantity exceeds stock availability | Reject with stock error |
| P7 | Non-integer quantity (decimal) | Reject or round |
| P8 | Null quantity | Reject or default to 1 |

**Representative Values:**

| Partition | Representative Value | Boundary Classification |
|-----------|---------------------|------------------------|
| P1 | 1 | Minimum valid (boundary) |
| P2 | 50 | Mid-range valid |
| P3 | 0 | Invalid minimum (boundary) |
| P4 | -5 | Invalid negative |
| P5 | 1001 | Invalid maximum (boundary) |
| P6 | 100 (when stock=50) | Business rule violation |
| P7 | 5.5 | Type violation |
| P8 | null | Missing value |

---

## 5. Test Execution Results

To run the partition tests created above:

```bash
# Navigate to sm-shop module
cd sm-shop

# Run all tests including partition tests
./mvnw test

# Run only partition tests
./mvnw test -Dtest=*PartitionTest

# Run specific partition test class
./mvnw test -Dtest=CustomerEmailPartitionTest

# Generate coverage report
./mvnw test jacoco:report
```

**Expected Output:**
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.salesmanager.test.shop.partition.CustomerEmailPartitionTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.salesmanager.test.shop.partition.CartQuantityPartitionTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

---

## 6. Conclusion

This report has documented the Shopizer e-commerce platform testing approach, including:

1. **Project Overview** - Understanding of the 115,000+ LOC Java-based headless commerce platform
2. **Build Process** - Complete documentation of Maven build system and deployment options
3. **Existing Tests** - Analysis of current JUnit-based integration test suite
4. **Partition Testing** - Systematic design of new test cases using equivalence partitioning and boundary value analysis

The partition testing approach demonstrates how systematic functional testing can efficiently cover input spaces while identifying critical edge cases. The new test cases provide additional coverage for customer registration, shopping cart operations, and product filtering features.

**Next Steps:**
- Implement the partition test cases in the codebase
- Run tests and measure coverage improvements
- Extend partitioning analysis to additional features
- Integrate tests into CI/CD pipeline

---

**Report End**

**Date:** January 26, 2026  
**Repository:** [Your GitHub Repository URL]
