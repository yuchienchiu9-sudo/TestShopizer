# Shopizer E-commerce Platform Testing Report
# Shopizer 电子商务平台测试报告

**Date / 日期:** January 26, 2026  
**Course / 课程:** Software Testing  
**GitHub Repository / GitHub 仓库:** [To be filled with your forked repository]

## Team Information / 团队信息

**Team Members / 团队成员:**
- [Add team member names and GitHub IDs]

**Collaborators / 协作者:**
- Prof. Jones (GitHub ID: jajones)
- TA Rakib Hossain (GitHub ID: MRHMisu)

---

## 1. Project Overview / 项目概述

### 1.1 What is Shopizer? / Shopizer 是什么？

**English:**
Shopizer is an open-source Java-based e-commerce platform designed for building headless commerce solutions and REST API-driven online stores. It provides comprehensive functionality for managing products, customers, orders, and merchant operations. Shopizer follows a modern microservices architecture and supports multiple deployment options including Docker containers.

**中文:**
Shopizer 是一个基于 Java 的开源电子商务平台，专为构建无头商务解决方案和 REST API 驱动的在线商店而设计。它提供了管理产品、客户、订单和商家操作的全面功能。Shopizer 遵循现代微服务架构，支持包括 Docker 容器在内的多种部署选项。

### 1.2 Project Purpose / 项目目的

**English:**
The primary purpose of Shopizer is to provide businesses with a flexible, scalable e-commerce backend that can be integrated with any frontend technology. It serves as a complete headless commerce solution with the following key capabilities:

- **Product Catalog Management** - Create and manage product listings, categories, and inventory
- **Shopping Cart & Checkout** - Handle customer shopping sessions and payment processing
- **Customer Management** - User registration, authentication, and profile management
- **Order Management** - Process and track customer orders
- **Merchant Administration** - Multi-store support and merchant configuration
- **REST API** - Complete API coverage for headless commerce implementations

**中文:**
Shopizer 的主要目的是为企业提供一个灵活、可扩展的电子商务后端，可以与任何前端技术集成。它作为一个完整的无头商务解决方案，具有以下关键功能：

- **产品目录管理** - 创建和管理产品列表、类别和库存
- **购物车和结账** - 处理客户购物会话和支付处理
- **客户管理** - 用户注册、身份验证和个人资料管理
- **订单管理** - 处理和跟踪客户订单
- **商家管理** - 多店铺支持和商家配置
- **REST API** - 为无头商务实现提供完整的 API 覆盖

### 1.3 Project Statistics / 项目统计

**English:**

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

**中文:**

| 指标 | 数值 |
|------|------|
| **代码总行数** | 约 115,000 行 |
| **主要编程语言** | Java (Java 11/17) |
| **框架** | Spring Boot 2.5.12 |
| **构建工具** | Maven |
| **架构** | 多模块 Maven 项目 |
| **数据库** | H2（默认），支持 MySQL、PostgreSQL、Oracle |
| **版本** | 3.2.5 |

**主要模块:**
- `sm-core` - 核心业务逻辑和服务
- `sm-core-model` - 领域模型和实体
- `sm-core-modules` - 集成模块（支付、物流等）
- `sm-shop` - REST API 和 Web 应用程序
- `sm-shop-model` - API 模型和 DTO

### 1.4 Technology Stack / 技术栈

**English:**
- **Backend:** Java 11+, Spring Boot, Spring Security, Spring Data JPA
- **ORM:** Hibernate
- **Database:** H2, MySQL 8.0, PostgreSQL, Oracle
- **Cache:** Infinispan, Ehcache
- **Search:** Elasticsearch 7.5
- **API Documentation:** Swagger 2.9.2
- **Testing:** JUnit 4/5, Spring Test
- **Build:** Maven
- **Containerization:** Docker

**中文:**
- **后端:** Java 11+, Spring Boot, Spring Security, Spring Data JPA
- **ORM:** Hibernate
- **数据库:** H2, MySQL 8.0, PostgreSQL, Oracle
- **缓存:** Infinispan, Ehcache
- **搜索:** Elasticsearch 7.5
- **API 文档:** Swagger 2.9.2
- **测试:** JUnit 4/5, Spring Test
- **构建:** Maven
- **容器化:** Docker

---

## 2. Build Documentation / 构建文档

### 2.1 Prerequisites / 前置要求

**English:**
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

**中文:**
在构建 Shopizer 之前，请确保已安装以下内容：

1. **Java 开发工具包 (JDK) 11 或更高版本**
   - 下载地址: https://adoptium.net/ 或 https://www.oracle.com/java/
   - 验证安装: `java -version`

2. **Maven**（可选 - 项目包含 Maven 包装器）
   - 项目包含 `mvnw`（Maven 包装器）脚本
   - 或从以下地址安装 Maven: https://maven.apache.org/

3. **Git**
   - 用于克隆仓库
   - 下载地址: https://git-scm.com/

4. **Docker**（可选 - 用于容器化部署）
   - 下载地址: https://www.docker.com/

### 2.2 Building the Application / 构建应用程序

**English:**

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

**中文:**

**步骤 1: 克隆仓库**
```bash
git clone https://github.com/[your-username]/shopizerForTest.git
cd shopizerForTest
```

**步骤 2: 构建整个项目**
```bash
# 使用 Maven 包装器（推荐）
./mvnw clean install

# 或使用系统 Maven
mvn clean install
```

此命令将:
- 编译所有 Java 源文件
- 运行单元测试和集成测试
- 将应用程序打包成 JAR 文件
- 将构件安装到本地 Maven 仓库

**构建时间:** 根据您的系统，大约需要 5-10 分钟

**步骤 3: 构建单个模块**（可选）
```bash
# 仅构建核心模块
cd sm-core
./mvnw clean install

# 仅构建 shop 模块
cd sm-shop
./mvnw clean install
```

### 2.3 Running the Application / 运行应用程序

**English:**

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

**中文:**

**方法 1: 使用 Maven Spring Boot 插件**
```bash
cd sm-shop
./mvnw spring-boot:run
```

**方法 2: 使用 Java JAR**
```bash
cd sm-shop
./mvnw clean package
java -jar target/sm-shop-3.2.5.jar
```

**方法 3: 使用 Docker**
```bash
# 拉取并运行官方 Docker 镜像
docker run -p 8080:8080 shopizerecomm/shopizer:latest
```

**方法 4: 构建并运行自定义 Docker 镜像**
```bash
# 构建 Docker 镜像
cd sm-shop
docker build -t shopizer-local .

# 运行容器
docker run -p 8080:8080 shopizer-local
```

**访问应用程序:**
- **API 文档 (Swagger):** http://localhost:8080/swagger-ui.html
- **API 基础 URL:** http://localhost:8080/api/v1
- **健康检查端点:** http://localhost:8080/actuator/health

**默认凭据:**
- 商店代码: `DEFAULT`
- 默认数据库: H2（内存数据库）

### 2.4 Configuration / 配置

**English:**

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

**中文:**

**数据库配置:**

默认情况下，Shopizer 使用 H2 内存数据库。要使用 MySQL 或 PostgreSQL：

1. 编辑 `sm-shop/src/main/resources/application.properties`
2. 配置数据库连接:

```properties
# MySQL 示例
spring.datasource.url=jdbc:mysql://localhost:3306/shopizer?useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

**基于配置文件的配置:**

Shopizer 支持多个配置文件（local、cloud、aws、gcp、mysql）：

```bash
# 使用特定配置文件运行
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql

# 或使用 JAR
java -jar -Dspring.profiles.active=mysql target/sm-shop-3.2.5.jar
```

---

## 3. Existing Test Cases Documentation / 现有测试用例文档

### 3.1 Testing Framework / 测试框架

**English:**

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

**中文:**

Shopizer 使用以下框架实施全面的测试策略：

- **JUnit 4 和 JUnit 5 (Jupiter)** - 核心测试框架
- **Spring Test** - 带 Spring 上下文的集成测试
- **Spring Boot Test** - Spring Boot 应用程序测试
- **Hamcrest** - 用于可读测试的断言匹配器
- **MockMvc** - 无需服务器的 REST API 测试

**测试结构:**
```
sm-shop/src/test/java/com/salesmanager/test/shop/
├── common/                    # 通用测试工具
│   └── ServicesTestSupport.java
├── integration/               # 集成测试
│   ├── cart/                  # 购物车测试
│   ├── customer/              # 客户管理测试
│   ├── order/                 # 订单处理测试
│   ├── product/               # 产品目录测试
│   ├── search/                # 搜索功能测试
│   ├── store/                 # 商家店铺测试
│   ├── system/                # 系统配置测试
│   └── user/                  # 用户认证测试
└── util/                      # 测试工具
```

### 3.2 Test Categories / 测试类别

**English:**

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

**中文:**

**1. 单元测试**
- 位于各个模块的 `src/test/java` 目录
- 隔离测试单个类和方法
- 模拟外部依赖

**2. 集成测试**
- 位于 `sm-shop/src/test/java/com/salesmanager/test/shop/integration/`
- 使用 Spring 上下文测试完整的 API 端点
- 使用 `@SpringBootTest` 和 `WebEnvironment.RANDOM_PORT`
- 使用 H2 内存数据库测试数据库交互

**3. 测试覆盖率配置**
```xml
<!-- 来自 pom.xml -->
<coverage.lines>.04</coverage.lines>      <!-- 4% 行覆盖率 -->
<coverage.branches>.01</coverage.branches> <!-- 1% 分支覆盖率 -->
```

### 3.3 Example Test Cases / 示例测试用例

**English:**

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

**中文:**

**示例 1: 客户注册测试**

文件: `CustomerRegistrationIntegrationTest.java`

```java
@SpringBootTest(classes = ShopApplication.class, 
                webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CustomerRegistrationIntegrationTest extends ServicesTestSupport {

    @Test
    public void registerCustomer() {
        // 创建测试客户数据
        final PersistableCustomer testCustomer = new PersistableCustomer();
        testCustomer.setEmailAddress("customer1@test.com");
        testCustomer.setPassword("clear123");
        testCustomer.setGender(CustomerGender.M.name());
        
        // 通过 REST API 注册客户
        final ResponseEntity<PersistableCustomer> response = 
            testRestTemplate.postForEntity("/api/v1/customer/register", 
                                          entity, 
                                          PersistableCustomer.class);
        
        // 验证注册成功
        assertThat(response.getStatusCode(), is(OK));
        
        // 使用注册凭据测试登录
        final ResponseEntity<AuthenticationResponse> loginResponse = 
            testRestTemplate.postForEntity("/api/v1/customer/login", 
                                          new AuthenticationRequest(...));
        assertNotNull(loginResponse.getBody().getToken());
    }
}
```

**测试覆盖范围:**
- 客户注册 API 端点
- 输入验证
- 数据库持久化
- 注册后的身份验证

**示例 2: 购物车测试**

文件: `ShoppingCartAPIIntegrationTest.java`

```java
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShoppingCartAPIIntegrationTest extends ServicesTestSupport {

    @Test
    @Order(1)
    public void createShoppingCart() {
        // 测试创建新购物车
        final ResponseEntity<ReadableShoppingCart> response = 
            testRestTemplate.postForEntity("/api/v1/cart", ...);
        
        assertThat(response.getStatusCode(), is(OK));
        assertNotNull(response.getBody().getCode());
    }
    
    @Test
    @Order(2)
    public void addItemToCart() {
        // 测试添加产品到购物车
        // 验证购物车总额正确更新
    }
}
```

**测试覆盖范围:**
- 购物车创建
- 添加/删除商品
- 数量更新
- 价格计算

### 3.4 Running Tests / 运行测试

**English:**

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

**中文:**

**运行所有测试:**
```bash
# 从项目根目录
./mvnw clean test

# 运行特定模块的测试
cd sm-shop
./mvnw test
```

**运行特定测试类:**
```bash
./mvnw test -Dtest=CustomerRegistrationIntegrationTest
```

**运行测试并生成覆盖率报告:**
```bash
./mvnw clean test jacoco:report
```

覆盖率报告将生成在:
- `target/site/jacoco/index.html`

**构建时跳过测试:**
```bash
./mvnw clean install -DskipTests
```

**仅运行集成测试:**
```bash
./mvnw verify -P integration-tests
```

**测试输出:**
- 控制台输出显示测试结果
- 测试报告在 `target/surefire-reports/`
- 用于 CI/CD 集成的 JUnit XML 报告

---

## 4. Partitioning and Test Case Design / 分区和测试用例设计

### 4.1 Motivation for Systematic Functional Testing / 系统功能测试的必要性

**English:**

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

**中文:**

**为什么系统化测试很重要:**

系统化功能测试对于确保软件质量和可靠性至关重要。随机或临时测试通常会遗漏关键边界情况，可能会对系统稳定性产生错误的信心。

**主要好处:**

1. **完整性** - 确保测试所有输入场景
2. **效率** - 通过选择代表性值减少冗余测试
3. **缺陷检测** - 识别常见缺陷发生的边界条件
4. **可维护性** - 提供结构化、文档化的测试方法
5. **信心** - 提供系统行为的可量化保证

**分区测试概念:**

分区测试将输入空间划分为等价类（分区），其中一个分区中的所有输入预期表现相似。这种方法：

- **减少测试用例** - 每个分区测试一个代表性值，而不是所有可能的输入
- **识别边界** - 关注分区之间的边界情况，这里经常隐藏错误
- **系统覆盖** - 确保测试所有不同的行为
- **领域驱动** - 基于需求和业务逻辑

**等价分区:**
- 将应产生类似系统行为的输入分组
- 从每个分区测试一个代表性值
- 假设如果一个值有效，该分区中的所有值都有效

**边界值分析:**
- 测试分区边界处的值
- 常见缺陷位置：最小值、最大值、边界内外
- 示例：对于年龄 18-65，测试：17、18、65、66

### 4.2 Feature Partitioning Examples / 功能分区示例

#### Feature 1: Customer Email Validation / 客户电子邮件验证

**English:**

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

**中文:**

**选择的功能:** 注册期间的客户电子邮件地址验证

**分区方案:**

| 分区 ID | 分区描述 | 预期行为 |
|---------|---------|---------|
| P1 | 有效的电子邮件格式 (local@domain.tld) | 接受并注册 |
| P2 | 缺少 @ 符号 | 拒绝并显示验证错误 |
| P3 | 缺少域名部分 | 拒绝并显示验证错误 |
| P4 | 缺少本地部分 | 拒绝并显示验证错误 |
| P5 | 本地部分包含无效字符 | 拒绝并显示验证错误 |
| P6 | 电子邮件已在系统中存在 | 拒绝并显示重复错误 |
| P7 | 空/null 电子邮件 | 拒绝并显示必填字段错误 |
| P8 | 电子邮件超过最大长度 | 拒绝并显示长度错误 |
| P9 | 包含特殊字符的有效电子邮件 | 接受并注册 |

**分区差异:**
- **P1 和 P9** 测试不同复杂程度的有效输入
- **P2-P5** 测试结构无效性（格式违规）
- **P6** 测试业务规则（唯一性约束）
- **P7** 测试必填字段验证
- **P8** 测试长度边界

**代表性值:**

| 分区 | 代表性值 | 理由 |
|------|---------|------|
| P1 | `user@example.com` | 标准有效电子邮件 |
| P2 | `userexample.com` | 缺少 @ 符号 |
| P3 | `user@` | 缺少域名 |
| P4 | `@example.com` | 缺少本地部分 |
| P5 | `user#$%@example.com` | 无效特殊字符 |
| P6 | `customer1@test.com`（已存在） | 重复的电子邮件 |
| P7 | `null` 或 `""` | 空值 |
| P8 | 255 字符的电子邮件 | 最大长度边界 |
| P9 | `user.name+tag@sub.example.com` | 有效的复杂格式 |

**边界值:**
- 电子邮件长度: 0、1、254、255、256 字符
- 本地部分长度: 0、1、64、65
- 域名部分长度: 0、1、253、254

**JUnit Test Implementation / JUnit 测试实现:**

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

    /**
     * P3: Missing domain part
     * Expected: HTTP 400 Bad Request
     */
    @Test
    public void testPartition3_MissingDomain() {
        PersistableCustomer customer = createTestCustomer("user@");
        
        ResponseEntity<PersistableCustomer> response = 
            testRestTemplate.postForEntity("/api/v1/customer/register", 
                new HttpEntity<>(customer, getHeader()), 
                PersistableCustomer.class);
        
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    /**
     * P4: Missing local part
     * Expected: HTTP 400 Bad Request
     */
    @Test
    public void testPartition4_MissingLocalPart() {
        PersistableCustomer customer = createTestCustomer("@example.com");
        
        ResponseEntity<PersistableCustomer> response = 
            testRestTemplate.postForEntity("/api/v1/customer/register", 
                new HttpEntity<>(customer, getHeader()), 
                PersistableCustomer.class);
        
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    /**
     * P6: Duplicate email
     * Expected: HTTP 409 Conflict or 400 Bad Request
     */
    @Test
    public void testPartition6_DuplicateEmail() {
        String email = "duplicate" + System.currentTimeMillis() + "@test.com";
        
        // First registration - should succeed
        PersistableCustomer customer1 = createTestCustomer(email);
        testRestTemplate.postForEntity("/api/v1/customer/register", 
            new HttpEntity<>(customer1, getHeader()), 
            PersistableCustomer.class);
        
        // Second registration with same email - should fail
        PersistableCustomer customer2 = createTestCustomer(email);
        ResponseEntity<PersistableCustomer> response = 
            testRestTemplate.postForEntity("/api/v1/customer/register", 
                new HttpEntity<>(customer2, getHeader()), 
                PersistableCustomer.class);
        
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    /**
     * P7: Empty/null email
     * Expected: HTTP 400 Bad Request
     */
    @Test
    public void testPartition7_EmptyEmail() {
        PersistableCustomer customer = createTestCustomer("");
        
        ResponseEntity<PersistableCustomer> response = 
            testRestTemplate.postForEntity("/api/v1/customer/register", 
                new HttpEntity<>(customer, getHeader()), 
                PersistableCustomer.class);
        
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    /**
     * P8: Email exceeds maximum length (boundary test)
     * Expected: HTTP 400 Bad Request
     */
    @Test
    public void testPartition8_ExceedsMaxLength() {
        // Create 256-character email (exceeds typical 255 limit)
        String longLocal = "a".repeat(250);
        String email = longLocal + "@example.com";
        
        PersistableCustomer customer = createTestCustomer(email);
        
        ResponseEntity<PersistableCustomer> response = 
            testRestTemplate.postForEntity("/api/v1/customer/register", 
                new HttpEntity<>(customer, getHeader()), 
                PersistableCustomer.class);
        
        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    /**
     * P9: Valid email with special characters and subdomain
     * Expected: HTTP 200 OK
     */
    @Test
    public void testPartition9_ValidComplexEmail() {
        PersistableCustomer customer = 
            createTestCustomer("user.name+tag@sub.example.com");
        
        ResponseEntity<PersistableCustomer> response = 
            testRestTemplate.postForEntity("/api/v1/customer/register", 
                new HttpEntity<>(customer, getHeader()), 
                PersistableCustomer.class);
        
        assertEquals(OK, response.getStatusCode());
    }

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

#### Feature 2: Shopping Cart Quantity Validation / 购物车数量验证

**English:**

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

**中文:**

**选择的功能:** 向购物车添加商品时的产品数量验证

**分区方案:**

| 分区 ID | 分区描述 | 预期行为 |
|---------|---------|---------|
| P1 | 数量 = 1 | 接受 |
| P2 | 有效范围内的数量 (2-999) | 接受 |
| P3 | 数量 = 0 | 拒绝（最小边界） |
| P4 | 数量 < 0（负数） | 拒绝 |
| P5 | 数量 > 最大允许值 (1000+) | 拒绝（最大边界） |
| P6 | 数量超过库存可用性 | 拒绝并显示库存错误 |
| P7 | 非整数数量（小数） | 拒绝或四舍五入 |
| P8 | Null 数量 | 拒绝或默认为 1 |

**代表性值:**

| 分区 | 代表性值 | 边界分类 |
|------|---------|---------|
| P1 | 1 | 最小有效值（边界） |
| P2 | 50 | 中等范围有效值 |
| P3 | 0 | 无效最小值（边界） |
| P4 | -5 | 无效负数 |
| P5 | 1001 | 无效最大值（边界） |
| P6 | 100（当库存=50时） | 违反业务规则 |
| P7 | 5.5 | 类型违规 |
| P8 | null | 缺失值 |

**JUnit Test Implementation / JUnit 测试实现:**

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
import com.salesmanager.shop.model.cart.PersistableShoppingCartItem;
import com.salesmanager.test.shop.common.ServicesTestSupport;

@SpringBootTest(classes = ShopApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CartQuantityPartitionTest extends ServicesTestSupport {

    /**
     * P1: Minimum valid quantity (boundary)
     * Expected: HTTP 200 OK
     */
    @Test
    public void testPartition1_MinimumValidQuantity() {
        PersistableShoppingCartItem item = createCartItem(1);
        
        ResponseEntity<String> response = addItemToCart(item);
        
        assertEquals(OK, response.getStatusCode());
    }

    /**
     * P2: Mid-range valid quantity
     * Expected: HTTP 200 OK
     */
    @Test
    public void testPartition2_MidRangeQuantity() {
        PersistableShoppingCartItem item = createCartItem(50);
        
        ResponseEntity<String> response = addItemToCart(item);
        
        assertEquals(OK, response.getStatusCode());
    }

    /**
     * P3: Zero quantity (minimum boundary violation)
     * Expected: HTTP 400 Bad Request
     */
    @Test
    public void testPartition3_ZeroQuantity() {
        PersistableShoppingCartItem item = createCartItem(0);
        
        ResponseEntity<String> response = addItemToCart(item);
        
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    /**
     * P4: Negative quantity
     * Expected: HTTP 400 Bad Request
     */
    @Test
    public void testPartition4_NegativeQuantity() {
        PersistableShoppingCartItem item = createCartItem(-5);
        
        ResponseEntity<String> response = addItemToCart(item);
        
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    /**
     * P5: Exceeds maximum allowed (boundary)
     * Expected: HTTP 400 Bad Request
     */
    @Test
    public void testPartition5_ExceedsMaximum() {
        PersistableShoppingCartItem item = createCartItem(1001);
        
        ResponseEntity<String> response = addItemToCart(item);
        
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    // Helper methods
    private PersistableShoppingCartItem createCartItem(int quantity) {
        PersistableShoppingCartItem item = new PersistableShoppingCartItem();
        item.setProductId("TEST_PRODUCT_123");
        item.setQuantity(quantity);
        return item;
    }

    private ResponseEntity<String> addItemToCart(PersistableShoppingCartItem item) {
        return testRestTemplate.postForEntity(
            "/api/v1/cart/DEFAULT/cart-code-123/item",
            new HttpEntity<>(item, getHeader()),
            String.class
        );
    }
}
```

#### Feature 3: Product Price Range Filter / 产品价格范围过滤

**English:**

**Selected Feature:** Price range filtering in product search/listing

**Partitioning Scheme:**

| Partition ID | Description | Expected Behavior |
|--------------|-------------|-------------------|
| P1 | Both min and max valid, min < max | Return products in range |
| P2 | Min = Max (single price point) | Return products at exact price |
| P3 | Min > Max (invalid range) | Reject or return empty |
| P4 | Min = 0, valid Max | Return products up to max |
| P5 | Valid Min, Max = infinity/null | Return products >= min |
| P6 | Both negative prices | Reject |
| P7 | Min negative, Max positive | Reject or treat min as 0 |
| P8 | Extremely large values | Handle gracefully |

**Representative Values:**

| Partition | Min Price | Max Price | Rationale |
|-----------|-----------|-----------|-----------|
| P1 | $10.00 | $100.00 | Standard valid range |
| P2 | $50.00 | $50.00 | Single price boundary |
| P3 | $100.00 | $50.00 | Inverted range error |
| P4 | $0.00 | $100.00 | Lower boundary test |
| P5 | $50.00 | null/∞ | Upper unbounded |
| P6 | -$10.00 | -$5.00 | Invalid negative |
| P7 | -$10.00 | $50.00 | Mixed validity |
| P8 | $0.01 | $999,999.99 | Extreme range |

**中文:**

**选择的功能:** 产品搜索/列表中的价格范围过滤

**分区方案:**

| 分区 ID | 描述 | 预期行为 |
|---------|------|---------|
| P1 | 最小值和最大值都有效，最小值 < 最大值 | 返回范围内的产品 |
| P2 | 最小值 = 最大值（单一价格点） | 返回确切价格的产品 |
| P3 | 最小值 > 最大值（无效范围） | 拒绝或返回空 |
| P4 | 最小值 = 0，最大值有效 | 返回最大值以下的产品 |
| P5 | 最小值有效，最大值 = 无穷大/null | 返回 >= 最小值的产品 |
| P6 | 两个都是负价格 | 拒绝 |
| P7 | 最小值负，最大值正 | 拒绝或将最小值视为 0 |
| P8 | 极大的值 | 优雅处理 |

**代表性值:**

| 分区 | 最小价格 | 最大价格 | 理由 |
|------|---------|---------|------|
| P1 | ¥10.00 | ¥100.00 | 标准有效范围 |
| P2 | ¥50.00 | ¥50.00 | 单一价格边界 |
| P3 | ¥100.00 | ¥50.00 | 倒置范围错误 |
| P4 | ¥0.00 | ¥100.00 | 下边界测试 |
| P5 | ¥50.00 | null/∞ | 上边界无限 |
| P6 | -¥10.00 | -¥5.00 | 无效负数 |
| P7 | -¥10.00 | ¥50.00 | 混合有效性 |
| P8 | ¥0.01 | ¥999,999.99 | 极端范围 |

---

## 5. Test Execution Results / 测试执行结果

**English:**

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

**中文:**

要运行上面创建的分区测试：

```bash
# 导航到 sm-shop 模块
cd sm-shop

# 运行所有测试，包括分区测试
./mvnw test

# 仅运行分区测试
./mvnw test -Dtest=*PartitionTest

# 运行特定的分区测试类
./mvnw test -Dtest=CustomerEmailPartitionTest

# 生成覆盖率报告
./mvnw test jacoco:report
```

**预期输出:**
```
[INFO] -------------------------------------------------------
[INFO]  测 试
[INFO] -------------------------------------------------------
[INFO] 运行 com.salesmanager.test.shop.partition.CustomerEmailPartitionTest
[INFO] 测试运行: 8, 失败: 0, 错误: 0, 跳过: 0
[INFO] 运行 com.salesmanager.test.shop.partition.CartQuantityPartitionTest
[INFO] 测试运行: 5, 失败: 0, 错误: 0, 跳过: 0
[INFO] 
[INFO] 结果:
[INFO] 
[INFO] 测试运行: 13, 失败: 0, 错误: 0, 跳过: 0
[INFO]
[INFO] 构建成功
```

---

## 6. Conclusion / 结论

**English:**

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

**中文:**

本报告记录了 Shopizer 电子商务平台的测试方法，包括：

1. **项目概述** - 了解基于 Java 的 115,000+ 行无头商务平台
2. **构建过程** - Maven 构建系统和部署选项的完整文档
3. **现有测试** - 当前基于 JUnit 的集成测试套件分析
4. **分区测试** - 使用等价分区和边界值分析系统设计新测试用例

分区测试方法展示了系统功能测试如何有效覆盖输入空间，同时识别关键边界情况。新的测试用例为客户注册、购物车操作和产品过滤功能提供了额外的覆盖。

**后续步骤:**
- 在代码库中实施分区测试用例
- 运行测试并测量覆盖率改进
- 将分区分析扩展到其他功能
- 将测试集成到 CI/CD 管道

---

## Appendix: GitHub Collaboration Setup / 附录：GitHub 协作设置

**English:**

**Steps to add collaborators:**

1. Fork the repository to your GitHub account
2. Navigate to repository Settings > Collaborators
3. Add team members by GitHub username
4. Add Prof. Jones (GitHub ID: `jajones`)
5. Add TA Rakib Hossain (GitHub ID: `MRHMisu`)

**Collaborative Workflow:**
- Create feature branches for partition tests
- Submit pull requests for review
- Maintain test documentation in repository
- Use GitHub Issues to track test cases

**中文:**

**添加协作者的步骤:**

1. 将仓库 Fork 到您的 GitHub 账户
2. 导航到仓库设置 > 协作者
3. 通过 GitHub 用户名添加团队成员
4. 添加 Jones 教授（GitHub ID: `jajones`）
5. 添加助教 Rakib Hossain（GitHub ID: `MRHMisu`）

**协作工作流:**
- 为分区测试创建功能分支
- 提交拉取请求以供审查
- 在仓库中维护测试文档
- 使用 GitHub Issues 跟踪测试用例

---

**Report End / 报告结束**

**Authors / 作者:** [Your Team Name]  
**Date / 日期:** January 26, 2026  
**Repository / 仓库:** [Your GitHub Repository URL]
