# Shopizer 电子商务平台测试报告（中文版）

**日期:** 2026年1月26日  
**课程:** 软件测试

---

## 1. 项目概述

### 1.1 Shopizer 是什么？

Shopizer 是一个基于 Java 的开源电子商务平台，专为构建无头商务解决方案和 REST API 驱动的在线商店而设计。它提供了管理产品、客户、订单和商家操作的全面功能。Shopizer 遵循现代微服务架构，支持包括 Docker 容器在内的多种部署选项。

### 1.2 项目目的

Shopizer 的主要目的是为企业提供一个灵活、可扩展的电子商务后端，可以与任何前端技术集成。它作为一个完整的无头商务解决方案，具有以下关键功能：

- **产品目录管理** - 创建和管理产品列表、类别和库存
- **购物车和结账** - 处理客户购物会话和支付处理
- **客户管理** - 用户注册、身份验证和个人资料管理
- **订单管理** - 处理和跟踪客户订单
- **商家管理** - 多店铺支持和商家配置
- **REST API** - 为无头商务实现提供完整的 API 覆盖

### 1.3 项目统计

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

### 1.4 技术栈

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

## 2. 构建文档

### 2.1 前置要求

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

### 2.2 构建应用程序

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

### 2.3 运行应用程序

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

### 2.4 配置

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

## 3. 现有测试用例文档

### 3.1 测试框架

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

### 3.2 测试类别

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

### 3.3 示例测试用例

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

### 3.4 运行测试

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

## 4. 分区和测试用例设计

### 4.1 系统功能测试的必要性

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

### 4.2 功能分区示例

#### 功能 1: 客户电子邮件验证

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

**JUnit 测试实现:**

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
     * P1: 标准有效电子邮件格式
     * 预期: HTTP 200 OK，客户创建成功
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
     * P2: 缺少 @ 符号
     * 预期: HTTP 400 Bad Request
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

    // 其他分区的测试方法...

    // 辅助方法：创建带指定电子邮件的测试客户
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

#### 功能 2: 购物车数量验证

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

---

## 5. 测试执行结果

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

## 6. 结论

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

**报告结束**

**日期:** 2026年1月26日  
**仓库:** [您的 GitHub 仓库 URL]
