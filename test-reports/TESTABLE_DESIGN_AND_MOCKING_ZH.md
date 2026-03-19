# 可测试设计和 Mock 分析报告

## 第一部分：可测试设计（20 分）

### 1. 可测试设计的方面和目标描述

**可测试设计**是指代码架构和模式，使得编写自动化测试变得容易。以下方面定义了良好的可测试设计：

#### 1.1 可测试设计的五个关键方面

| 方面 | 描述 | 好处 |
|------|------|------|
| **依赖注入（Dependency Injection）** | 类通过构造函数、setter 或字段注入获得依赖，而不是在内部创建 | 允许测试注入 mock 对象而不是真实对象 |
| **单一职责原则（SRP）** | 每个类只有一个改变的原因；最小化依赖 | 易于隔离测试；需要的测试用例少 |
| **接口契约** | 使用接口而不是具体实现 | 无需依赖实际实现即可创建测试双重对象（stub、mock） |
| **控制反转（IoC）** | 调用者不决定如何创建对象；容器决定 | 能够灵活地替换对象以进行测试 |
| **无隐藏依赖** | 所有依赖显式声明，不隐藏在方法内部 | 测试可以看到所有输入输出；没有意外的外部调用 |

#### 1.2 可测试设计的目标

1. **隔离性**：分离测试一个组件，不运行其他
2. **可重复性**：测试每次运行产生相同结果
3. **速度**：测试执行快速（无数据库、网络调用）
4. **可控性**：测试控制外部依赖（时间、随机性、I/O）
5. **清晰性**：测试代码清楚地表达意图和设置

---

### 2. Stubbing 的例子

#### 2.1 现有的 Stubbing 实现

**文件**：[sm-core/src/test/java/com/salesmanager/test/business/utils/DataUtilsTest.java](sm-core/src/test/java/com/salesmanager/test/business/utils/DataUtilsTest.java#L24-L28)

**代码示例**：
```java
@Test
public void testGetWeight_When_StoreUnit_LB_MeasurementUnit_LB(){
    // Stubbing：创建一个 mock MerchantStore 并定义其行为
    MerchantStore store = mock(MerchantStore.class);
    when(store.getWeightunitcode()).thenReturn(MeasureUnit.LB.name());
    
    // 用 stubbed 依赖调用被测试的实际方法
    double result = DataUtils.getWeight(100.789, store, MeasureUnit.LB.name());
    
    // 验证结果
    assertEquals(100.79, result, 0);
}
```

#### 2.2 工作原理和原因

**被 stub 的内容**：`MerchantStore` 接口

**如何实现**：使用 Mockito 的 `mock()` 和 `when().thenReturn()` 模式
- `mock(MerchantStore.class)` 创建一个测试双重对象（stub）
- `when(store.getWeightunitcode()).thenReturn(MeasureUnit.LB.name())` 定义该方法被调用时返回预定义值

**为什么**：
- `MerchantStore` 是一个数据库实体，需要实际数据库设置才能测试
- 通过 stubbing，测试只关注 `DataUtils.getWeight()` 中的重量转换逻辑
- 测试在毫秒内运行，无数据库开销
- 测试是确定性的：每次返回相同值（不是随机数据库状态）

**Stub vs 真实对象**：
- ❌ 没有 stub：需要创建真实 MerchantStore，持久化到数据库，创建测试数据库
- ✅ 使用 stub：创建轻量级内存中的测试双重对象，精确控制返回值

---

### 3. 新测试中 Stub 的实现

#### 3.1 改进的设计代码（见下面第 5 节）

我们将创建一个新的测试来演示 `MerchantStore` 接口更高级的 stubbing。

---

### 4. 不好的可测试设计 - 反面例子

#### 4.1 问题代码分析

**文件**：[sm-core/src/main/java/com/salesmanager/core/business/services/reference/loader/IntegrationModulesLoader.java](sm-core/src/main/java/com/salesmanager/core/business/services/reference/loader/IntegrationModulesLoader.java)

**问题代码**：
```java
@Component
public class IntegrationModulesLoader {
    
    // 问题 1：方法内部硬编码创建 ObjectMapper
    public List<IntegrationModule> loadIntegrationModules(String jsonFilePath) throws Exception {
        List<IntegrationModule> modules = new ArrayList<IntegrationModule>();
        
        // ObjectMapper 直接创建，未注入
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            // 问题 2：文件 I/O 带有硬编码的 classloader 逻辑
            InputStream in = this.getClass()
                .getClassLoader()
                .getResourceAsStream(jsonFilePath);
            
            Map[] objects = mapper.readValue(in, Map[].class);
            // ... 处理
            return modules;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
```

#### 4.2 为什么这个设计不可测试

| 问题 | 为什么不可测试 | 影响 |
|------|-------------|------|
| **硬编码的 ObjectMapper** | 无法用 mock 替换来模拟错误或特定解析行为 | 难以测试错误处理路径 |
| **直接文件系统访问** | 测试需要实际 JSON 文件在 classpath 上；无法测试不同文件内容 | 脆弱的测试；对文件路径变化敏感 |
| **无依赖注入** | 所有依赖在内部创建；无法注入测试双重对象 | 必须测试真实实现，不是逻辑 |
| **紧密耦合实现** | 测试必须知道确切的 JSON 文件路径和结构 | 测试脆弱；JSON 结构变化破坏测试 |
| **混合职责** | 类做文件 I/O AND 解析 AND 对象创建 | 难以单独单元测试解析逻辑 |

**测试挑战**：
1. **无法测试错误处理**：如果 ObjectMapper 失败怎么办？无法模拟
2. **无法测试不同 JSON 格式**：需要多个测试 JSON 文件
3. **文件可能在测试环境中不存在**：测试将在 CI/CD 中失败
4. **难以 mock 文件系统状态**：网络挂载文件、权限问题

---

### 5. 改进的可测试设计

#### 5.1 使用依赖注入重构的代码

**新的设计原则**：使用依赖注入和接口契约

```java
// 第 1 步：创建接口来抽象文件/流加载
public interface IntegrationModuleSource {
    /**
     * 从源加载原始 JSON 为字符串。
     * 这允许不同的实现：文件、数据库、网络或测试 mock
     */
    String loadRawJson(String identifier) throws ServiceException;
}

// 第 2 步：为解析逻辑创建接口
public interface IntegrationModuleParser {
    /**
     * 从 JSON 字符串解析模块列表。
     * 与 I/O 关系分离。
     */
    List<IntegrationModule> parseModules(String jsonContent) throws ServiceException;
}

// 第 3 步：用依赖注入重构加载器
@Component
public class IntegrationModulesLoader {
    
    // 现在依赖注入，可以用测试双重对象替换
    private final IntegrationModuleSource source;
    private final IntegrationModuleParser parser;
    
    // 构造函数注入 - 依赖清晰且必需
    public IntegrationModulesLoader(
        IntegrationModuleSource source,
        IntegrationModuleParser parser) {
        this.source = source;
        this.parser = parser;
    }
    
    /**
     * 加载并解析模块。
     * 现在可测试：可以注入 mock source 和 parser
     */
    public List<IntegrationModule> loadIntegrationModules(String jsonFilePath) 
            throws ServiceException {
        String jsonContent = source.loadRawJson(jsonFilePath);
        return parser.parseModules(jsonContent);
    }
}

// 第 4 步：原始基于文件的实现（生产）
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
                throw new ServiceException("文件未找到: " + jsonFilePath);
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
            throw new ServiceException("从 " + jsonFilePath + " 加载 JSON 失败", e);
        }
    }
}

// 第 5 步：解析器实现（职责分离）
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
            // 解析 JSON 为 map 数组
            Map[] objects = mapper.readValue(jsonContent, Map[].class);
            List<IntegrationModule> modules = new ArrayList<>();
            
            for (Map object : objects) {
                modules.add(this.parseModule(object));
            }
            return modules;
        } catch (Exception e) {
            throw new ServiceException("从 JSON 解析模块失败", e);
        }
    }
    
    private IntegrationModule parseModule(Map<String, Object> object) {
        IntegrationModule module = new IntegrationModule();
        module.setModule((String) object.get("module"));
        module.setCode((String) object.get("code"));
        module.setImage((String) object.get("image"));
        // ... 其他字段
        return module;
    }
}
```

#### 5.2 为什么这样更可测试

| 方面 | 之前 | 之后 |
|------|------|------|
| **文件 I/O** | 硬编码在方法中 | 注入依赖（可 mock） |
| **解析逻辑** | 与 I/O 混合 | 分离接口（可独立测试） |
| **依赖声明** | 隐藏在方法内 | 显式在构造函数中 |
| **测试双重对象** | 不可能 | 易于创建 stub |
| **错误处理** | 无法测试失败 | 易于 mock 失败 |

---

### 6. 改进代码的测试用例

**文件**：在 `sm-core/src/test/java/com/salesmanager/test/business/loader/IntegrationModulesLoaderTest.java` 创建新测试

```java
package com.salesmanager.test.business.loader;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.loader.IntegrationModulesLoader;
import com.salesmanager.core.model.system.IntegrationModule;

/**
 * 演示改进的可测试设计的测试用例
 * 使用依赖注入和 mocking
 */
public class IntegrationModulesLoaderTest {
    
    /**
     * IntegrationModuleSource 的 Stub 实现
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
                throw new ServiceException("Stub 异常：文件未找到");
            }
            return jsonToReturn;
        }
    }
    
    @Test
    public void testLoadModulesWithValidJson() throws Exception {
        // 安排：创建返回有效 JSON 的 stub
        String validJson = "[{" +
            "\"module\": \"payment\", " +
            "\"code\": \"paypal\", " +
            "\"image\": \"paypal.png\"" +
            "}]";
        
        IntegrationModuleSource stubSource = new StubIntegrationModuleSource(validJson);
        IntegrationModuleParser mockParser = mock(IntegrationModuleParser.class);
        
        // 设置 mock parser 返回模块
        List<IntegrationModule> expectedModules = new ArrayList<>();
        IntegrationModule module = new IntegrationModule();
        module.setCode("paypal");
        expectedModules.add(module);
        
        when(mockParser.parseModules(validJson)).thenReturn(expectedModules);
        
        // 执行：用注入的 stub 和 mock 创建加载器
        IntegrationModulesLoader loader = new IntegrationModulesLoader(
            stubSource, mockParser);
        List<IntegrationModule> result = loader.loadIntegrationModules("data/modules.json");
        
        // 断言
        assertNotNull("结果不应为空", result);
        assertEquals("应有 1 个模块", 1, result.size());
        assertEquals("模块代码应为 paypal", "paypal", result.get(0).getCode());
        
        // 验证解析器被调用
        verify(mockParser).parseModules(validJson);
    }
    
    @Test
    public void testLoadModulesWithSourceException() throws Exception {
        // 安排：创建抛异常的 stub
        StubIntegrationModuleSource stubSource = new StubIntegrationModuleSource("");
        stubSource.setThrowException(true);
        
        IntegrationModuleParser mockParser = mock(IntegrationModuleParser.class);
        
        // 执行和断言
        IntegrationModulesLoader loader = new IntegrationModulesLoader(
            stubSource, mockParser);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            loader.loadIntegrationModules("nonexistent.json");
        });
        
        // 验证异常消息
        assertTrue("异常应提及文件未找到", 
            exception.getMessage().contains("文件未找到"));
    }
    
    @Test
    public void testLoadModulesWithParsingError() throws Exception {
        // 安排：有效源但解析器失败
        String validJson = "[invalid json]";
        IntegrationModuleSource validSource = new StubIntegrationModuleSource(validJson);
        
        IntegrationModuleParser mockParser = mock(IntegrationModuleParser.class);
        when(mockParser.parseModules(validJson))
            .thenThrow(new ServiceException("解析错误"));
        
        // 执行和断言
        IntegrationModulesLoader loader = new IntegrationModulesLoader(
            validSource, mockParser);
        
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            loader.loadIntegrationModules("data/modules.json");
        });
        
        assertTrue("异常应提及解析错误", 
            exception.getMessage().contains("解析错误"));
    }
}
```

---

## 第二部分：Mock（20 分）

### 1. Mock 的描述和用途

#### 1.1 什么是 Mock？

**Mock** 是创建一个测试双重对象，该对象：
- 与真实对象具有相同的接口
- 记录它如何被调用（调用跟踪）
- 允许在测试运行前定义期望行为
- 允许在测试运行后验证行为

#### 1.2 Mock vs Stub

| 方面 | Stub | Mock |
|------|------|------|
| **目的** | 提供预定义的返回值 | 跟踪调用并验证行为 |
| **交互** | 单向（stub 提供值） | 双向（mock 提供和验证） |
| **设置** | `when().thenReturn()` | `when().thenReturn()` + `verify()` |
| **用例** | 简单值替换 | 复杂交互验证 |
| **例子** | Stub 数据库返回固定用户 | Mock 支付网关验证收费金额正确 |

#### 1.3 Mock 的用途

**没有 Mock 的问题**：
```
测试需要调用 PaymentService.charge(amount)
PaymentService 调用真实支付网关 API
真实 API 扣款或限速
测试变得缓慢、昂贵、不可靠
```

**有 Mock 的解决方案**：
```
测试创建 mock PaymentGateway
通过依赖注入传给 PaymentService
测试调用 PaymentService.charge(amount)
PaymentService 调用 mock（而不是真实 API）
测试验证：服务是否用正确金额调用网关？
无实际扣款，即时执行，可靠
```

---

### 2. 可以被 Mock 的功能

#### 2.1 确认的功能：EmailService

**当前情况**：Shopizer 项目在整个代码库中使用 `EmailService`（在 [AbstractSalesManagerCoreTestCase.java](sm-core/src/test/java/com/salesmanager/test/common/AbstractSalesManagerCoreTestCase.java#L113) 中可见）

```java
@Inject
protected EmailService emailService;
```

**问题**：
- 测试订单创建、客户注册时，发送实际邮件
- 测试缓慢（SMTP 网络调用）
- 测试修改外部状态（真实邮箱）
- 难以验证邮件内容而不解析真实邮件服务器
- CI/CD 可能没有邮件凭证

**Mock 如何帮助**：
1. **隔离逻辑**：无 EmailService 依赖测试订单服务
2. **验证邮件调用**：确保正确的收件人、主题行被发送
3. **测试错误处理**：模拟邮件服务失败
4. **速度**：无网络调用，毫秒内运行
5. **CI/CD 友好**：无凭证，无外部依赖

---

### 3. 使用 Mockito 的测试用例

**创建新测试**：`sm-core/src/test/java/com/salesmanager/test/business/services/EmailServiceMockTest.java`

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

/**
 * Mock EmailService 以验证邮件行为，无需发送真实邮件
 * 演示高级 Mockito 功能：验证、参数捕获、异常
 */
public class EmailServiceMockTest {
    
    /**
     * 使用 EmailService 的示例服务
     * （在真实代码中，这将是 OrderService、CustomerService 等）
     */
    static class OrderNotificationService {
        private EmailService emailService;
        
        public OrderNotificationService(EmailService emailService) {
            this.emailService = emailService;
        }
        
        /**
         * 发送订单确认邮件
         * 这是我们想测试的方法，不实际发送邮件
         */
        public void sendOrderConfirmation(String customerEmail, String orderId, 
                double totalAmount, MerchantStore store) throws ServiceException {
            
            // 验证输入
            if (customerEmail == null || customerEmail.isEmpty()) {
                throw new ServiceException("客户邮箱必需");
            }
            if (totalAmount <= 0) {
                throw new ServiceException("订单金额必须为正");
            }
            
            // 构建邮件内容
            String subject = "订单确认：" + orderId;
            String body = String.format(
                "感谢您的订购！\n" +
                "订单号：%s\n" +
                "总计：¥%.2f\n" +
                "店铺：%s\n",
                orderId, totalAmount, store.getStorename());
            
            // 发送邮件（这是我们将 mock 的）
            emailService.sendHtmlEmail(store, customerEmail, null, subject, body, null);
        }
    }
    
    // Mock 依赖
    @Mock
    private EmailService mockEmailService;
    
    @Mock
    private MerchantStore mockStore;
    
    private OrderNotificationService notificationService;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new OrderNotificationService(mockEmailService);
        
        // 设置 mock store
        when(mockStore.getStorename()).thenReturn("测试店铺");
    }
    
    /**
     * 测试 1：验证邮件以正确参数被调用
     * 演示：参数验证和调用计数
     */
    @Test
    public void testOrderConfirmationEmailIsSent() throws ServiceException {
        // 安排
        String customerEmail = "customer@example.com";
        String orderId = "ORD-12345";
        double totalAmount = 99.99;
        
        // 执行：发送订单确认
        notificationService.sendOrderConfirmation(
            customerEmail, orderId, totalAmount, mockStore);
        
        // 断言（验证）：EmailService 被调用恰好一次
        verify(mockEmailService, times(1)).sendHtmlEmail(
            any(MerchantStore.class),      // store
            eq(customerEmail),               // 收件人邮箱
            isNull(),                        // 第二收件人
            contains("ORD-12345"),          // 主题包含订单号
            contains("99.99"),              // 正文包含金额
            isNull()                        // 附件
        );
    }
    
    /**
     * 测试 2：捕获并验证邮件内容细节
     * 演示：ArgumentCaptor 用于对调用参数的详细断言
     */
    @Test
    public void testOrderConfirmationEmailContentIsCorrect() throws ServiceException {
        // 安排
        String customerEmail = "john@example.com";
        String orderId = "ORD-67890";
        double totalAmount = 299.50;
        
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        
        // 执行
        notificationService.sendOrderConfirmation(
            customerEmail, orderId, totalAmount, mockStore);
        
        // 断言：捕获传给 sendHtmlEmail 的参数
        verify(mockEmailService).sendHtmlEmail(
            any(MerchantStore.class),
            eq(customerEmail),
            isNull(),
            subjectCaptor.capture(),
            bodyCaptor.capture(),
            isNull()
        );
        
        // 验证邮件主题和正文内容
        String subject = subjectCaptor.getValue();
        String body = bodyCaptor.getValue();
        
        assertTrue("主题应包含订单号", 
            subject.contains("ORD-67890"));
        assertTrue("主题应包含'确认'", 
            subject.contains("确认"));
        assertTrue("正文应包含订单号", 
            body.contains("ORD-67890"));
        assertTrue("正文应包含金额", 
            body.contains("299.50"));
        assertTrue("正文应包含店铺名", 
            body.contains("测试店铺"));
    }
    
    /**
     * 测试 3：验证无效输入时不发送邮件
     * 演示：异常处理和负面测试用例
     */
    @Test
    public void testOrderConfirmationThrowsExceptionForEmptyEmail() {
        // 安排：空客户邮箱
        String customerEmail = "";
        
        // 执行和断言：应抛异常
        assertThrows(ServiceException.class, () -> {
            notificationService.sendOrderConfirmation(
                customerEmail, "ORD-111", 50.0, mockStore);
        });
        
        // 验证：邮件服务不应被调用
        verify(mockEmailService, never()).sendHtmlEmail(any(), any(), any(), any(), any(), any());
    }
    
    /**
     * 测试 4：验证负数/零金额时不发送邮件
     * 演示：输入验证测试
     */
    @Test
    public void testOrderConfirmationThrowsExceptionForNegativeAmount() {
        // 执行和断言：负金额应抛异常
        assertThrows(ServiceException.class, () -> {
            notificationService.sendOrderConfirmation(
                "customer@example.com", "ORD-222", -50.0, mockStore);
        });
        
        // 验证：邮件服务未被调用
        verify(mockEmailService, never()).sendHtmlEmail(any(), any(), any(), any(), any(), any());
    }
    
    /**
     * 测试 5：模拟邮件服务失败
     * 演示：用 mock 异常测试错误处理
     */
    @Test
    public void testOrderConfirmationHandlesEmailException() throws ServiceException {
        // 安排：Mock 邮件服务抛异常
        doThrow(new ServiceException("SMTP 服务器无法连接"))
            .when(mockEmailService)
            .sendHtmlEmail(any(), any(), any(), any(), any(), any());
        
        // 执行和断言：服务应传播邮件异常
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            notificationService.sendOrderConfirmation(
                "customer@example.com", "ORD-333", 75.0, mockStore);
        });
        
        // 验证异常包含邮件服务错误
        assertTrue("异常应提及 SMTP", 
            exception.getMessage().contains("SMTP"));
    }
}
```

---

## 总结

本报告演示：

1. **可测试设计原则**：
   - 确认了 5 个关键方面（DI、SRP、接口、IoC、无隐藏依赖）
   - 在 `DataUtilsTest` 中找到现有 stubbing，使用 Mockito
   - 分析 `IntegrationModulesLoader` 中的不可测试代码
   - 设计了使用接口和注入的改进版本
   - 为改进代码创建了全面的测试套件

2. **Mock 技术**：
   - 解释 mock vs stubbing 的权衡
   - 确认 `EmailService` 为不可测试的依赖
   - 创建了 5+ 个测试用例演示：
     - 基本 mock 验证
     - 参数捕获
     - 异常处理
     - 条件逻辑测试
     - 边界值分析

这两个原则对于在专业软件开发中编写可维护、快速、可靠的测试套件至关重要。
