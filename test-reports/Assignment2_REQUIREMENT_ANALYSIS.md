# Assignment 2 要求分析
## 关于"定价测试"vs"上架FSM"的争议解决

---

## 📋 作业的四大要求

| 要求 | 分值 | 内容 | 备注 |
|------|------|------|------|
| 1 | 10% | 描述有限模型如何有用于测试 | 理论部分 |
| 2 | 20% | **选择一个适合用"非平凡"功能模型描述的特性** | ⚠️ 关键 |
| 3 | 35% | 创建、绘制、描述该功能模型 | 设计部分 |
| 4 | 35% | 写JUnit测试用例覆盖该模型 | 代码部分 |

---

## 🎯 核心问题：定价能不能单独做?

### ❌ 定价测试为什么**不符合** "non-trivial functional model" 要求

**定价的本质：**
```java
// 定价就是一个值的验证，没有"状态"
public class Product {
    private BigDecimal price;  // 这只是一个属性
}

// 你的测试验证：
// ✓ price >= 0
// ✓ price <= 999999.99
// ✓ price precision (2位小数)
// ✓ price != null

// 但这是"等价分区"和"边界值分析"
// 不是"有限状态机"！
```

**FSM需要什么：**
```
FSM = (States, Events, Transitions, Guards)

例子1: 产品状态 ✅ 真正的FSM
  States: DRAFT, PENDING, ACTIVE, INACTIVE, ARCHIVED
  Events: submitForApproval, approve, reject, deactivate, archive
  Transitions: DRAFT --[submitForApproval]--> PENDING
  Guards: 必须价格有效, 必须有库存, etc.

例子2: 购物车状态 ✅ 真正的FSM
  States: EMPTY, PENDING, CONFIRMED, CLOSED
  Events: addItem, removeItem, checkout, cancel
  Transitions: EMPTY --[addItem]--> PENDING
  
例子3: 定价 ❌ 不是FSM
  只有一个值，没有状态转换
  不符合"non-trivial functional model"
```

---

## 🚀 解决方案：你应该做什么

### 选项A: 纯定价测试 ❌ 不推荐

**问题：**
```
✓ 满足 Requirement 1 (10%)  - 可以解释FSM的用处
✓ 满足 Requirement 4 (35%)  - 可以写JUnit测试
✓ 满足 Requirement 3 (35%)  - 但定价没有FSM可画
✗ 不满足 Requirement 2 (20%) - 定价不是"non-trivial functional model"

总分：70/100 ❌ (缺少20分关键要求)
```

### 选项B: 产品上架FSM ✅ 完全推荐

**产品上架流程是真正的非平凡FSM：**

```
状态图：
┌────────────────────────────────────────────┐
│ 产品生命周期 (Product Lifecycle FSM)        │
└────────────────────────────────────────────┘

           ┌─────────────────────────┐
           │       DRAFT             │  初始状态：刚创建，还在编辑
           │  (可编辑价格、分类等)    │
           └────────────┬────────────┘
                        │ submitForApproval
                        │ [Guard: price必须有效]  ← 你的价格测试关联点！
                        ▼
           ┌─────────────────────────┐
           │      PENDING            │  待审核：等待管理员批准
           │    (不可编辑)            │
           └────────────┬────────────┘
                        │
              ┌─────────┴─────────┐
              │                   │
        approve                 reject
             │                   │
        [Guards...]         [Guards...]
             │                   │
             ▼                   ▼
    ┌──────────────┐     ┌──────────────┐
    │    ACTIVE    │     │   REJECTED   │
    │ (对外可见)   │     │  (不可见)    │
    └──────────────┘     └──────────────┘
             │
             │ [updatePrice再次修改]
             │ 
             ▼
    ┌──────────────┐
    │   MODIFIED   │  已修改：价格或其他属性改动后的状态
    └──────────────┘
```

**为什么这是真正的非平凡FSM：**

1. **7个明确的状态**
   - DRAFT, PENDING, ACTIVE, MODIFIED, REJECTED, INACTIVE, ARCHIVED

2. **多个转换条件（Guard）**
   ```java
   DRAFT → PENDING:
     ✓ 价格必须有效 (0 < price <= 999999.99)
     ✓ 产品必须有分类
     ✓ 库存必须≥0
     ✓ 必须有描述
   
   PENDING → ACTIVE:
     ✓ 管理员审批通过
     ✓ 价格仍然有效
     ✓ 库存充足
   
   ACTIVE → MODIFIED:
     ✓ 价格被修改
     ✓ 新价格必须有效 ← 再次调用你的价格验证！
   
   ACTIVE → INACTIVE:
     ✓ 可手动下架
   ```

3. **完整的覆盖模型**
   - 测试每一个合法的状态转换
   - 测试每一个非法的转换（应该被拒绝）
   - 测试所有的guard条件
   - 31+个JUnit测试用例

**分值分配：**
```
✓ Requirement 1 (10%)  - 解释FSM如何帮助测试
✓ Requirement 2 (20%)  - 产品上架是"非平凡"的FSM
✓ Requirement 3 (35%)  - 可以绘制7个状态的状态图
✓ Requirement 4 (35%)  - 可以写31+个JUnit测试覆盖所有转换

总分：100/100 ✅
```

---

## 💡 你的价格测试如何融入?

**关键洞察：** 你的价格测试不是"替代品"，而是"基础"！

```
Assignment 1: 产品价格分区测试
  ├─ 19个测试用例
  ├─ 验证价格的有效性 (P1-P7分区)
  └─ 发现5个价格相关的bug

Assignment 2: 产品上架FSM
  ├─ 7个状态
  ├─ 31+个JUnit测试用例
  ├─ **在每个状态转换的Guard中调用价格验证**
  │  例如：
  │  @Test
  │  void testDraftToPendingWithInvalidPrice() {
  │      // 创建产品，设置非法价格 (来自你的P5-P7分区)
  │      product.setPrice(new BigDecimal("-10")); // P5: 负数
  │      
  │      // 尝试提交审核 (DRAFT → PENDING)
  │      // 应该失败，因为价格无效
  │      assertThrows(InvalidPriceException.class, 
  │          () -> productService.submitForApproval(product));
  │  }
  │
  └─ **重用你在Assignment 1中发现的bug**
```

---

## 🎓 关于"性能测试"的疑问

**作业没有要求性能测试！**

你看要求中：
- ✗ 没有说"性能"
- ✗ 没有说"并发"
- ✗ 没有说"压力测试"

只要求：
- ✓ FSM理论(10%)
- ✓ 选择合适的功能(20%)
- ✓ 设计FSM(35%)
- ✓ 写JUnit覆盖FSM(35%)

---

## 📊 最终建议

| 选项 | 定价 | 上架FSM | 能否通过 | 分数 | 建议 |
|------|------|---------|---------|------|------|
| **纯定价** | ✅ | ❌ | ❌缺少FSM | 70% | 不推荐 |
| **定价+上架** | ✅ | ✅ | ✅完整 | 100% | ✅推荐 |
| **定价+购买** | ✅ | ❌ | ❌与朋友重复 | 70% | 不推荐 |

---

## ✅ 推荐的Assignment 2执行计划

### 步骤1: 理论部分 (10%)
写一个章节解释："为什么有限状态机有用于测试?"
- FSM帮助识别所有可能的状态
- FSM帮助识别所有可能的转换
- FSM帮助找到测试的覆盖空白

### 步骤2: 特性选择 (20%)
明确说明："为什么选择产品上架流程作为FSM?"
- 7个状态 → 非平凡
- 多个guard条件 → 复杂
- 与价格测试直接相关 → 自然延伸
- 避免与朋友重复 → 工作分工清晰

### 步骤3: FSM设计 (35%)
- 绘制状态图 (7个状态)
- 绘制状态转换表 (21个可能的转换)
- 文档化每个guard条件

### 步骤4: JUnit实现 (35%)
创建 `ProductLifecycleStateMachineTest.java`
```java
@DisplayName("Product Lifecycle FSM Tests")
class ProductLifecycleStateMachineTest {
    
    // 状态验证测试 (7个)
    @Test void testProductInDraftState() { }
    @Test void testProductInPendingState() { }
    @Test void testProductInActiveState() { }
    // ... 更多状态测试
    
    // 转换测试 (21个)
    @Test void testDraftToPendingWithValidPrice() { }
    @Test void testDraftToPendingWithInvalidPrice() { }
    @Test void testPendingToActiveApproval() { }
    @Test void testPendingToRejectionWithReason() { }
    // ... 更多转换测试
    
    // Guard条件测试 (多个)
    @Test void testCannotSubmitWithoutPrice() { }
    @Test void testCannotSubmitWithNegativePrice() { }
    @Test void testCannotSubmitWithoutCategory() { }
    // ... 更多guard测试
}
```

---

## 总结

**你的疑虑 vs 答案：**

| 你的疑虑 | 答案 |
|---------|------|
| "为什么要上升到购买请求?" | 不要上升到购买！选择上架(你的职责)，购买是朋友的(购物车) |
| "定价测试不能涵盖FSM吗?" | 对，纯定价没有状态转换，不是FSM。但上架流程是！ |
| "性能测试怎么办?" | 作业没要求性能测试，只要求FSM |
| "有限状态机如何与定价相关?" | 上架FSM的guard条件依赖价格验证。定价是基础。 |

**最后：你现在完全可以专注于产品上架FSM，而不是购买流程！**
