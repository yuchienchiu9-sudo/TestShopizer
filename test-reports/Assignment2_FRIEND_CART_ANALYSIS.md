# 朋友的Assignment 2方案分析
## 购物车FSM是否能满足Assignment 2要求？

---

## 📊 朋友现有的两个测试

### 1️⃣ MyCartQuantityTest.java
**现状分析：**
```
✓ 11个测试用例
✓ 4个分区（P1-P4）
✓ 边界值分析
✓ 完成了Assignment 1的工作

但是：
✗ 这是"分区测试"，不是"有限状态机"
✗ 没有状态转换
✗ 不符合Assignment 2要求
```

### 2️⃣ MyCartStateTest.java
**现状分析：**
```
✓ 有状态（EMPTY, ACTIVE, OBSOLETE）
✓ 有状态转换（EMPTY → ACTIVE → EMPTY → OBSOLETE）
✓ 有状态自环（ACTIVE → ACTIVE 当更新数量时）
✓ 有2个测试用例覆盖不同路径

关键问题：
⚠️ 状态太少（只有3个）
⚠️ 转换太少（只有4个主要转换）
⚠️ Guard条件太简单（几乎没有）
⚠️ 可能不够"non-trivial"
```

---

## 🔍 详细对比：朋友的购物车FSM vs 你的产品上架FSM

### 购物车生命周期FSM（朋友的）

**当前模型：**
```
EMPTY --[addItem]--> ACTIVE
  ↑                    |
  |                    |
  +--[removeItem]<--[updateQty (self)]
  
ACTIVE --[deleteCart]--> OBSOLETE
  
EMPTY --[deleteCart]--> OBSOLETE
```

**分析：**
| 方面 | 评分 | 问题 |
|------|------|------|
| **状态数量** | 3个 | ⚠️ 太少了。3个状态算不上"非平凡" |
| **转换数量** | 4个 | ⚠️ 只有4个基本转换 |
| **Guard条件** | ⚠️ 少 | ⚠️ 几乎没有业务规则检查 |
| **复杂度** | 低 | ⚠️ 很直白，容易理解 |
| **非平凡性** | 中等 | ⚠️ 可能达不到"non-trivial"的要求 |

### 产品上架FSM（你的）

**设计的模型：**
```
DRAFT --[submitForApproval 条件: 价格有效]--> PENDING
  |                                              |
  |                                        /---┬─\
  |                                       /    |   \
  +----[reject]---------  [approve]  [reject]
                               |         |
                               v         v
                            ACTIVE   REJECTED
                               |
                               |[updatePrice]
                               v
                            MODIFIED
                               |
                          [other events]
                               |
                               v
                           INACTIVE
                               |
                          [archive]
                               v
                           ARCHIVED
```

**分析：**
| 方面 | 评分 | 说明 |
|------|------|------|
| **状态数量** | ✅ 7个 | 充分体现"非平凡" |
| **转换数量** | ✅ 21个 | 复杂的转换逻辑 |
| **Guard条件** | ✅ 多个 | 价格有效、库存充足、权限检查等 |
| **复杂度** | ✅ 高 | 真实的电商业务流程 |
| **非平凡性** | ✅ 符合 | 明确的非平凡FSM |

---

## ❓ 朋友的购物车测试能否通过Assignment 2?

### 方案1：仅用MyCartStateTest（当前的3状态FSM）❌

**分值评估：**
```
Requirement 1 (10%)  ✅ 解释FSM的用处       → 10/10
Requirement 2 (20%)  ⚠️ 选择合适特性        → 10/20 (风险：太简单)
Requirement 3 (35%)  ⚠️ 设计FSM            → 25/35 (只有3个状态)
Requirement 4 (35%)  ✅ JUnit测试          → 30/35 (测试不够全面)
───────────────────────────────────────────────────
总分：约75-80分 ⚠️ (可能不足，取决于老师的"非平凡"定义)
```

**风险：** 老师可能认为"3个状态 + 4个转换"不符合"non-trivial"

### 方案2：扩展购物车FSM（添加更多状态和转换）✅

**改进方向：**
```
现有状态：EMPTY, ACTIVE, OBSOLETE (3个)

可添加状态：
+ PENDING_CHECKOUT   (待结账)
+ SUBMITTED          (已提交订单)
+ CANCELLED          (已取消)
+ MERGED             (与其他购物车合并)

改进的FSM：
EMPTY 
  └──[addItem]──> ACTIVE
                    ├──[updateQty]──> ACTIVE (self)
                    ├──[removeItem]──> EMPTY
                    ├──[initCheckout]──> PENDING_CHECKOUT  ← 新转换
                    └──[clear]──> OBSOLETE
  
  PENDING_CHECKOUT
    ├──[confirmCheckout]──> SUBMITTED  ← 新转换
    ├──[cancelCheckout]──> ACTIVE       ← 新转换
    └──[abandon]──> OBSOLETE
  
  SUBMITTED
    ├──[confirmPayment]──> MERGED       ← 新转换
    ├──[cancelOrder]──> CANCELLED       ← 新转换
    └──[deleteCart]──> OBSOLETE

  CANCELLED
    ├──[reopenCart]──> ACTIVE           ← 新转换
    └──[deleteCart]──> OBSOLETE
  
  MERGED
    └──[deleteCart]──> OBSOLETE
```

**改进后的评估：**
```
状态数: 7个 ✅
转换数: ~15个 ✅
Guard条件: 可以添加（支付验证、库存检查等） ✅
非平凡性: ✅ 符合要求

分值估计：85-95分
```

---

## 🎯 我的建议：朋友应该怎么做？

### 选项A：保守方案（扩展当前FSM）⭐ 推荐

**做法：**
1. 在当前3状态的基础上，添加3-4个新状态
2. 实现购物车的完整生命周期（不只是创建/删除）
3. 添加Guard条件：支付验证、库存检查等
4. 写15-20个JUnit测试覆盖所有转换

**实现步骤：**
```java
// 1. 定义更多状态
public enum CartState {
    EMPTY,              // 空购物车
    ACTIVE,             // 有商品
    PENDING_CHECKOUT,   // 待结账
    SUBMITTED,          // 已提交
    CANCELLED,          // 已取消
    MERGED,             // 已合并
    OBSOLETE            // 已删除
}

// 2. 创建 ShoppingCartStateMachineTest.java
@Test void testEmptyToActive() { }
@Test void testActiveToEmpty() { }
@Test void testActiveToPendingCheckout() { }
@Test void testPendingCheckoutToSubmitted() { }
@Test void testPendingCheckoutToCancelled() { }
@Test void testSubmittedToMerged() { }
@Test void testMergedToObsolete() { }
// ... 更多转换测试

// 3. 添加Guard条件测试
@Test void testCannotCheckoutWithEmptyCart() { }
@Test void testCannotCheckoutIfStockInsufficient() { }
```

**分值：90-100分 ✅**

---

### 选项B：激进方案（订单处理FSM）⭐⭐ 更好

**做法：**
朋友做"购物车 → 订单"的完整FSM，这样可以：
1. 包含购物车的所有状态
2. 扩展到订单处理的状态
3. 真正的电商完整流程

**完整流程FSM：**
```
购物车阶段：
  EMPTY → ACTIVE → PENDING_CHECKOUT → SUBMITTED

订单阶段：
  ORDER_CREATED → PAID → SHIPPED → DELIVERED → ARCHIVED
  
可能的失败转换：
  PENDING_CHECKOUT → CANCELLED (超时或用户取消)
  PAID → REFUNDED (退款)
  SHIPPED → LOST (丢失)
```

**分值：95-100分 ✅✅**

---

## 📝 关键点总结

| 问题 | 朋友的情况 | 答案 |
|------|---------|------|
| **能不能只做购物车?** | 目前的FSM太简单 | 需要扩展或改进 |
| **3个状态够吗?** | 不够 | 至少需要5-7个 |
| **与你的工作冲突吗?** | 完全不冲突 | 你做产品，他做购物车/订单 |
| **两个人都能通过吗?** | 如果他扩展FSM | 可以！各有各的FSM |

---

## ✅ 最终建议

**给你朋友的建议：**

1. **最低方案：** 扩展当前MyCartStateTest
   - 从3个状态扩展到6-7个状态
   - 从4个转换扩展到15个转换
   - 添加Guard条件检查
   - 预期分数：85-95分

2. **推荐方案：** 创建ShoppingCartAndOrderFSM
   - 包含购物车的完整生命周期
   - 包含订单处理的状态转换
   - 实现真实的电商流程
   - 预期分数：95-100分

**你们的分工应该是：**
```
你：产品价格 + 产品上架FSM (7个状态)
朋友：购物车 + 订单处理FSM (7-8个状态)

两个完全独立的FSM！
两个完全不同的模块！
零冲突！
```
