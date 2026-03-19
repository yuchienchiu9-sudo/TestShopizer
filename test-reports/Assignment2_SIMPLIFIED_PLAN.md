# Assignment 2 最简化方案
## 既满足要求，工作量又不大的做法

---

## 🎯 核心问题

朋友已经有了基础的购物车FSM（3个状态），但担心扩展到6-7个状态工作量太大。

**解决方案：不用全部重写，只需在现有基础上微调！**

---

## 💡 最小化改进方案

### 朋友目前的FSM

```
EMPTY --[addItem]--> ACTIVE --[removeItem]--> EMPTY
                       |
                    [updateQty]
                       |
                       v
                     ACTIVE (self-loop)

ACTIVE/EMPTY --[deleteCart]--> OBSOLETE
```

**问题：** 只有3个状态，太简单了

### 最小化改进（只加1个状态）

```
                        [checkout]
                            |
                            v
EMPTY --[addItem]--> ACTIVE --[removeItem]--> EMPTY
                       |  ↑
                       |  └----[cancel]
                  [updateQty]
                       |
                       v
                  PENDING_CHECKOUT

ACTIVE/PENDING_CHECKOUT --[deleteCart]--> OBSOLETE
```

**只需添加：**
1. ✅ 一个新状态：`PENDING_CHECKOUT`（待结账状态）
2. ✅ 两个新转换：
   - `ACTIVE → PENDING_CHECKOUT` (调用checkout())
   - `PENDING_CHECKOUT → ACTIVE` (调用cancelCheckout())
3. ✅ 一个新的测试用例：验证这两个转换

---

## 📊 改进后的FSM规模

| 指标 | 现有 | 改进后 | 变化 |
|------|------|--------|------|
| **状态数** | 3 | 4 | +1 |
| **转换数** | 4 | 6 | +2 |
| **主要测试** | 2 | 3-4 | +1-2 |
| **总工作量** | 100% | 120% | +20% |

---

## 🚀 具体实现步骤

### 步骤1：扩展现有的MyCartStateTest（只加20行代码）

```java
// 在现有的MyCartStateTest.java中添加

@Test
public void testActiveToCheckoutAndBack() throws Exception {
    System.out.println("====== Test: ACTIVE -> PENDING_CHECKOUT -> ACTIVE ======");
    
    // 1. 创建ACTIVE状态的购物车
    ShoppingCart cart = new ShoppingCart();
    cart.setMerchantStore(store);
    cart.setShoppingCartCode(UUID.randomUUID().toString());
    
    ShoppingCartItem item = new ShoppingCartItem(cart, product);
    item.setQuantity(1);
    cart.getLineItems().add(item);
    
    shoppingCartService.saveOrUpdate(cart);
    
    // 验证状态: ACTIVE
    ShoppingCart activeCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
    Assert.assertFalse("Should be ACTIVE", activeCart.getLineItems().isEmpty());
    System.out.println(" State Verified: ACTIVE");
    
    // 2. 转换: ACTIVE -> PENDING_CHECKOUT
    // (调用某种"checkout初始化"方法)
    activeCart.setCartStatus("PENDING_CHECKOUT");  // 或者其他方式标记
    shoppingCartService.saveOrUpdate(activeCart);
    
    ShoppingCart checkoutCart = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
    Assert.assertEquals("PENDING_CHECKOUT", checkoutCart.getCartStatus());
    System.out.println(" Transition Verified: ACTIVE -> PENDING_CHECKOUT");
    
    // 3. 转换: PENDING_CHECKOUT -> ACTIVE (取消结账)
    checkoutCart.setCartStatus("ACTIVE");
    shoppingCartService.saveOrUpdate(checkoutCart);
    
    ShoppingCart backToActive = shoppingCartService.getByCode(cart.getShoppingCartCode(), store);
    Assert.assertEquals("ACTIVE", backToActive.getCartStatus());
    System.out.println(" Transition Verified: PENDING_CHECKOUT -> ACTIVE");
}
```

### 步骤2：更新FSM文档（只需1-2页）

```markdown
# 购物车生命周期 FSM

## 状态定义
- **EMPTY**: 购物车为空或刚创建
- **ACTIVE**: 购物车包含商品，可以修改
- **PENDING_CHECKOUT**: 已开始结账流程，待支付
- **OBSOLETE**: 购物车已删除

## 状态转换表
| 源状态 | 目标状态 | 事件 | 条件 |
|-------|--------|------|------|
| EMPTY | ACTIVE | addItem | 有商品可加 |
| ACTIVE | EMPTY | removeItem | 移除最后一个商品 |
| ACTIVE | PENDING_CHECKOUT | initiateCheckout | 用户点击结账 |
| PENDING_CHECKOUT | ACTIVE | cancelCheckout | 用户取消结账 |
| ACTIVE | OBSOLETE | deleteCart | 管理员删除 |
| PENDING_CHECKOUT | OBSOLETE | deleteCart | 管理员删除 |
| EMPTY | OBSOLETE | deleteCart | 管理员删除 |

## 测试覆盖
✓ EMPTY → ACTIVE (testShoppingCartFSM第一部分)
✓ ACTIVE → EMPTY (testShoppingCartFSM第一部分)
✓ ACTIVE → OBSOLETE (testActiveToObsolete)
✓ ACTIVE → PENDING_CHECKOUT → ACTIVE (testActiveToCheckoutAndBack) ← 新增
✓ PENDING_CHECKOUT → OBSOLETE (可选)
```

---

## ✅ 这个简化方案的优势

| 方面 | 评估 |
|------|------|
| **工作量** | 💚 很小（只加1个状态、2个转换、1个测试） |
| **复杂度** | 💛 中等（足以体现"非平凡"） |
| **现实性** | 💚 高（购物车确实有结账这一步） |
| **满足要求** | ✅ 可以（4个状态>3个状态，明显是FSM） |
| **实现难度** | 💚 低（可能只需改现有code） |
| **预期分数** | 📊 80-90分（安全通过） |

---

## 📋 与大方案的对比

| 特征 | 大方案 | 简化方案 |
|------|-------|---------|
| 状态数 | 6-7 | 4 |
| 转换数 | 15-20 | 6 |
| 新增测试 | 15-20个 | 1-2个 |
| 代码量 | ~500行 | ~100行 |
| 文档量 | 多 | 少 |
| 完成时间 | 3-5天 | 1-2天 |
| 风险度 | 低 | 超低 |

---

## 🎯 最终建议

**给朋友的建议：**

1. **不用全部重写** MyCartStateTest
2. **只需添加** 一个新的测试方法（testActiveToCheckoutAndBack）
3. **只需修改** ShoppingCart模型，添加`cartStatus`字段
4. **只需更新** 一份2-3页的FSM文档
5. **总工作时间** 2-3小时

**为什么这样可以通过？**
- ✅ 有4个清晰的状态（vs 要求的"非平凡"）
- ✅ 有6个有意义的转换（不只是简单的create/delete）
- ✅ 体现了真实的购物车生命周期
- ✅ JUnit测试覆盖了主要路径
- ✅ 文档清晰明了

---

## 🚀 你和朋友的最终分工

```
你：
  ├─ ProductPricePartitionTest (已完成)
  ├─ ProductLifecycleStateMachineTest (新建)
  │   ├─ 7个产品状态
  │   ├─ 21个状态转换
  │   └─ 31+个JUnit测试
  └─ 预期时间：3-5天

朋友：
  ├─ MyCartQuantityTest (已完成)
  ├─ MyCartStateTest (已有基础)
  │   └─ 扩展为4个购物车状态
  │   └─ 添加2个新转换
  │   └─ 增加1个新测试
  └─ 预期时间：1-2天

总体分工：
- 你的工作量：70% (复杂的产品FSM)
- 朋友的工作量：30% (简化的购物车FSM)
- 都能通过：✅ Yes
- 无冲突：✅ Yes
```

---

## ⚠️ 一个关键提醒

如果朋友觉得即使是简化方案也嫌麻烦，可以再往下简化：

**终极简化（真的最小化）：**
- 不需要扩展状态
- 就用现有的3个状态
- 但在文档中深度分析这3个状态为什么构成"non-trivial FSM"
  - 解释Guard条件
  - 解释为什么某些转换被禁止
  - 解释状态之间的业务规则
- 测试就用现有的2个测试用例
- **预期分数：70-80分（可能有风险）**

**但我不太推荐这个，因为会有被老师质疑的风险。**

---

## 最后的建议

**就采用"最小化改进方案"吧：**
```
✅ 只加1个状态 (PENDING_CHECKOUT)
✅ 只加2个转换
✅ 只加1个测试
✅ 工作量很小（1-2小时代码，1小时文档）
✅ 风险很小（不会被质疑"太简单"）
✅ 能得80-90分
```

这样你朋友可以轻松完成，不会累死自己。
