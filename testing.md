## 3. Existing Test Cases Documentation

### 3.1 Testing Framework & Strategy
Our analysis of the codebase reveals a hybrid testing environment utilizing both **JUnit 4** (legacy) and **JUnit 5 Jupiter** (modern). The system employs a comprehensive testing stack:

* **Core Frameworks:** JUnit 4, JUnit 5, Spring Boot Test (v2.5.12), and Spring Test.
* **Integration Testing:** The project relies heavily on `TestRestTemplate` to test REST API endpoints in a real web environment (`WebEnvironment.RANDOM_PORT`).
* **Base Class Design:** All integration tests extend `ServicesTestSupport` (`com.salesmanager.test.shop.common.ServicesTestSupport`). This critical base class handles:
    * **Authentication:** Automatically acquires JWT tokens via the `admin@shopizer.com` account.
    * **Data Setup:** Provides helper methods to create persistent test data (Stores, Products, Categories) before tests run.

### 3.2 Test Organization & Coverage
The tests are primarily located in `sm-shop/src/test/java/com/salesmanager/test/shop/integration/`. Key test modules include:

* **Shopping Cart:** `ShoppingCartAPIIntegrationTest` uses `@TestMethodOrder` to simulate a complete user flow (Create -> Add -> Update -> Delete).
* **Customer Lifecycle:** `CustomerRegistrationIntegrationTest` validates the full registration-to-login process.
* **Store Management:** `MerchantStoreApiIntegrationTest` covers store creation and logo uploads.

**Coverage Analysis:**
Based on `pom.xml` configuration and our execution results, the current test coverage is relatively low (approx. 4% line coverage). A significant number of test classes are marked with `@Ignore`, indicating a need for more robust maintenance and systematic functional testing.

---

## 4. Partitioning and Test Case Design

### 4.1 Motivation for Systematic Functional Testing
Systematic functional testing, specifically **Partition Testing**, is essential for Shopizer because random testing often misses critical edge cases. By dividing the input space into equivalence partitions, we can:
1. **Reduce Redundancy:** Test one representative value instead of infinite possibilities.
2. **Catch Logic Errors:** Focus on boundaries (e.g., 0, negative numbers) where bugs are most likely to occur.

### 4.2 Feature Partitioning Examples

#### Feature 1: Shopping Cart Quantity Validation
**Contributor:** [Yu Chien Chiu / yuchienchiu9-sudo]

**Selected Feature:** Product quantity validation when adding items to the shopping cart via `ShoppingCartService`.

**Partitioning Scheme:**
We focused on the `quantity` input field. Given a product with a stock inventory of **10**, we partitioned the inputs into four distinct classes:

| Partition ID | Partition Description | Representative Value | Expected Behavior | Actual Behavior |
| :--- | :--- | :--- | :--- | :--- |
| **P1 (Valid)** | Positive integer within stock | `5` | **Success** (Cart updated) | Success ✅ |
| **P2 (Invalid)** | Negative integer | `-1` | **Exception** (Reject) | **Accepted** (Bug Found) ❌ |
| **P3 (Invalid)** | Zero quantity | `0` | **Exception** (Reject) | **Accepted** (Bug Found) ❌ |
| **P4 (Invalid)** | Exceeds inventory (>10) | `11` | **Exception** (Stock Error) | **Accepted** (Bug Found) ❌ |

**Rationale:**
* **P1 (Happy Path):** Ensures the basic functionality works.
* **P2 & P3 (Logical Invalidity):** A commerce system should never accept non-positive quantities.
* **P4 (Business Rule):** The system must enforce inventory limits to prevent overselling.

**Test Execution & Findings:**
We implemented these partitions in a new test class `MyCartQuantityTest.java`.

* **Result:** The valid partition (P1) passed. However, **all three invalid partitions (P2, P3, P4) failed to throw an exception.**
* [cite_start]**Critical Defect:** The system **silently accepts** negative numbers, zero quantities, and orders exceeding stock[cite: 18]. This indicates a missing validation layer in the `ShoppingCartService`, posing a risk to data integrity.

![JUnit Test Results](https://github.com/[your-repo]/blob/main/path-to-image/junit_results.png)
*Figure 1: JUnit Test Results. The red crosses indicate that the system failed to throw exceptions for invalid inputs (-1, 0, >10), confirming the bug.*

#### Feature 2: [Pending Friend's Feature]
*(Note: This section is reserved for the second team member to document their partitioning strategy and test results.)*







## 3. 現有測試案例文件 (Existing Test Cases Documentation)

### 3.1 測試框架與策略
我們對程式碼庫的分析顯示，目前採用的是混合測試環境，同時使用了 **JUnit 4** (舊版) 和 **JUnit 5 Jupiter** (新版)。系統採用了全面的測試技術堆疊：

* **核心框架 (Core Frameworks)**：JUnit 4, JUnit 5, Spring Boot Test (v2.5.12), 以及 Spring Test。
* **整合測試 (Integration Testing)**：專案高度依賴 `TestRestTemplate` 來在真實的 Web 環境 (`WebEnvironment.RANDOM_PORT`) 中測試 REST API 端點。
* **基礎類別設計 (Base Class Design)**：所有的整合測試都繼承自 `ServicesTestSupport` (`com.salesmanager.test.shop.common.ServicesTestSupport`)。這個關鍵的基礎類別負責處理：
    * **驗證 (Authentication)**：透過 `admin@shopizer.com` 帳號自動獲取 JWT Token。
    * **資料設置 (Data Setup)**：提供輔助方法，在測試執行前建立持久化的測試資料（如商店、產品、分類）。

### 3.2 測試組織與覆蓋率
測試主要位於 `sm-shop/src/test/java/com/salesmanager/test/shop/integration/` 目錄下。關鍵的測試模組包括：

* **購物車 (Shopping Cart)**：`ShoppingCartAPIIntegrationTest` 使用 `@TestMethodOrder` 來模擬完整的使用者流程（建立 -> 新增 -> 更新 -> 刪除）。
* **客戶生命週期 (Customer Lifecycle)**：`CustomerRegistrationIntegrationTest` 驗證從註冊到登入的完整流程。
* **商店管理 (Store Management)**：`MerchantStoreApiIntegrationTest` 涵蓋商店建立與 Logo 上傳。

**覆蓋率分析 (Coverage Analysis)：**
根據 `pom.xml` 的設定以及我們的執行結果，目前的測試覆蓋率相對較低（行覆蓋率約 4%）。有大量的測試類別被標記為 `@Ignore`，這顯示專案需要更穩健的維護以及系統化的功能測試。

---

## 4. 分割測試與測試案例設計 (Partitioning and Test Case Design)

### 4.1 系統化功能測試的動機
對於 Shopizer 而言，系統化功能測試，特別是 **分割測試 (Partition Testing)** 是至關重要的，因為隨機測試通常會遺漏關鍵的邊緣情況。透過將輸入空間劃分為等價分區，我們可以：
1. **減少冗餘**：測試一個具代表性的數值，而不是測試無限的可能性。
2. **捕捉邏輯錯誤**：專注於最容易發生 Bug 的邊界（例如：0、負數）。

### 4.2 功能分區範例

#### 功能 1：購物車數量驗證 (Shopping Cart Quantity Validation)
**貢獻者：** [Yu Chien Chiu / yuchienchiu9-sudo]

**選擇的功能：** 透過 `ShoppingCartService` 將商品加入購物車時的產品數量驗證。

**分區方案 (Partitioning Scheme)：**
我們專注於「數量」輸入欄位。假設一個產品的庫存量為 **10**，我們將輸入劃分為四個不同的類別：

| 分區 ID | 分區描述 | 代表值 | 預期行為 | 實際行為 |
| :--- | :--- | :--- | :--- | :--- |
| **P1 (有效)** | 庫存內的正整數 | `5` | **成功** (購物車更新) | 成功 ✅ |
| **P2 (無效)** | 負整數 | `-1` | **異常** (拒絕) | **被接受** (發現 Bug) ❌ |
| **P3 (無效)** | 零數量 | `0` | **異常** (拒絕) | **被接受** (發現 Bug) ❌ |
| **P4 (無效)** | 超過庫存 (>10) | `11` | **異常** (庫存錯誤) | **被接受** (發現 Bug) ❌ |

**設計理由 (Rationale)：**
* **P1 (快樂路徑/正常流程)**：確保基本功能運作正常。
* **P2 & P3 (邏輯無效性)**：電子商務系統永遠不應接受非正數的數量。
* **P4 (業務規則)**：系統必須強制執行庫存限制以防止超賣。

**測試執行與發現 (Test Execution & Findings)：**
我們在一個新的測試類別 `MyCartQuantityTest.java` 中實作了這些分區。

* **結果**：有效分區 (P1) 通過。然而，**所有三個無效分區 (P2, P3, P4) 都未能拋出異常**。
* **重大缺陷 (Critical Defect)**：系統**默默接受 (Silently accepts)** 了負數、零數量以及超過庫存的訂單。這顯示 `ShoppingCartService` 中缺少驗證層，對資料完整性構成風險。

![JUnit 測試結果](https://github.com/[your-repo]/blob/main/path-to-image/junit_results.png)
*圖 1：JUnit 測試結果。紅色的叉號顯示系統未能針對無效輸入 (-1, 0, >10) 拋出異常，證實了 Bug 的存在。*

#### 功能 2：[待朋友補充的功能]
*(註：此區塊保留給第二位組員記錄其分區策略與測試結果。)*