# 作业 5：可测试性设计与 Mocking 报告

## 第一部分：可测试性设计 (20 分)

### 1.1 可测试性设计的维度与目标
可测试设计是指允许软件组件与其运行环境隔离开来，并进行独立验证的设计方式。主要目标包括：
*   **隔离性 (解耦)**：组件不应直接依赖于外部系统的具体实现（如数据库、文件系统、网络），而应依赖于抽象（接口）。
*   **可控性**：测试者应能强制软件进入特定状态（例如：模拟网络超时或特定的数据库返回值）。
*   **确定性**：确保在输入和环境状态相同的情况下，组件每次产生的输出都是一致的。
*   **可观测性**：组件的内部状态或输出应易于获取，以便进行验证。

### 1.2 Stubbing (桩对象) 实现

**Stubbing 的现有应用：**
在重构后的 `IntegrationModulesLoader` 中，我们引入了 `IntegrationModuleSource` 接口。
*   **用法**：系统现在使用此接口加载原始数据，而不是直接访问类路径资源。
*   **原理**：原实现与 Java ClassLoader 和资源文件紧密耦合。通过对该源进行 Stubbing，我们可以在内存中返回预定义的 JSON 字符串，使测试变得快速且不依赖于物理磁盘。

**[图片预留位置 1]**  
*描述：展示 IntegrationModulesLoader.java 中 IntegrationModuleSource 接口及其默认实现的截图。*

**新 Stub 实现：**
我在 `IntegrationModulesLoaderTest.java` 中实现了 `StubIntegrationModuleSource`。
*   **技术细节**：创建了一个实现 `IntegrationModuleSource` 接口的静态内部类。
*   **测试案例**：在 `testLoadModulesWithValidJson` 中，该 Stub 被注入到 loader 中。它返回一个硬编码的 JSON 字符串，允许我们在不读取真实文件的情况下验证解析器的逻辑。

**[图片预留位置 2]**  
*描述：StubIntegrationModuleSource 类以及使用它的测试案例截图。*

### 1.3 坏的可测试性分析

**记录的代码 (原始设计)：**
原始的 `IntegrationModulesLoader.loadIntegrationModules` 方法包含以下逻辑：
```java
InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFilePath);
Map[] objects = mapper.readValue(in, Map[].class);
```
**存在的问题**：
这种设计导致测试困难的原因如下：
1.  **文件系统依赖**：如果不创建物理文件，就无法测试代码如何处理不同的 JSON 结构。
2.  **错误处理**：在不操纵环境的情况下，几乎不可能模拟 `InputStream` 失败或特定的 I/O 错误。
3.  **副作用**：测试可能会意外修改或依赖共享资源文件，导致测试结果不稳定。

**修复建议**：
使用 **依赖注入 (DI)** 取代硬编码。定义一个接口来抽象资源加载行为，并将该接口通过构造函数传递。

**新设计的实现**：
我更新了 `IntegrationModulesLoader`，使其接收两个接口：`IntegrationModuleSource`（获取字符串）和 `IntegrationModuleParser`（将数据转换为对象）。

**[图片预留位置 3]**  
*描述：重构后的 IntegrationModulesLoader 构造函数截图，显示依赖注入的应用。*

---

## 第二部分：Mocking (20 分)

### 2.1 Mocking 及其用途
Mocking 是指创建模拟对象来替代真实、复杂的对象。其主要用途有：
*   **验证交互**：与 Stub（仅提供数据）不同，Mock 允许验证方法是否被调用、使用了哪些参数以及调用了多少次。
*   **避免副作用**：模拟 `EmailService` 可确保在测试运行期间不会真的发出邮件。
*   **模拟外部系统**：Mock 可以轻松模拟外部 API 故障或难以用真实系统触发的特定调用序列。

### 2.2 适合 Mocking 的功能：邮件通知
`EmailService` 是 Mocking 的绝佳候选对象。在大多数企业应用中，发送邮件涉及 SMTP 服务器和网络连接。
*   **行为检查**：如果不使用 Mocking，我们只能检查代码是否运行而不崩溃。使用 Mockito 模拟后，我们可以检查是否设置了**正确的收件人**，以及**邮件模板令牌**（如订单 ID 和金额）是否正确填充。

**[图片预留位置 4]**  
*描述：EmailService 接口定义的截图。*

### 2.3 Mockito 测试案例
我使用 Mockito 实现了 `EmailServiceMockTest.java`。
*   **关键技术**：对 `EmailService` 使用了 `@Mock`，并使用 `ArgumentCaptor` 来检查传递给服务的 `Email` 对象。
*   **验证**：测试 `testOrderConfirmationEmailContentIsCorrect` 捕获了 `Email` 对象，并断言其主题和模板令牌与测试数据一致。

**[图片预留位置 5]**  
*描述：使用 ArgumentCaptor 和 verify(mockEmailService).sendHtmlEmail(...) 的 JUnit 测试截图。*

---

### 结论
通过将代码重构为使用接口和依赖注入，我们将一个旧的“难以测试”的组件转变为模块化服务。使用 Mockito 让我们能够在不产生发送真实邮件的开销或风险的情况下，验证业务逻辑（如邮件生成）。
