# GitHub Actions CI 流程详解

## 一、什么是 GitHub Actions？

GitHub Actions 是 GitHub 提供的**自动化工作流程工具**。简单来说，它可以在你的代码库中发生特定事件（比如推送代码）时，自动执行一系列预定义的任务。

把它想象成一个"机器人助手"：
- 你写好规则（workflow 文件）
- 只要代码有变化，机器人就自动按规则行动
- 你坐在家里，不需要手动运行任何命令

---

## 二、你部署的 CI 工作流文件在哪里？

**文件位置**：`.github/workflows/ci.yml`

```yaml
name: CI

on:
  push:
    branches: '*'

jobs:
  build-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
      - run: chmod +x ./mvnw
      - run: ./mvnw -B -pl sm-core -am -Dtest=DataUtilsTest -DfailIfNoTests=false test
```

---

## 三、这个 CI 具体做了什么？

### 🔴 触发条件（Trigger）
```
on:
  push:
    branches: '*'
```
**含义**：每当你推送代码到任何分支，这个工作流程就自动启动。

**现实例子**：
- 你在本地修改代码，运行 `git push`
- 代码上传到 GitHub 的瞬间，GitHub Actions 自动启动
- 你不需要做任何其他操作

---

### 🟡 执行环境（Runner）
```
runs-on: ubuntu-latest
```
**含义**：在 GitHub 的云服务器（Ubuntu 最新版）上执行这些任务，完全免费。

**好处**：
- 不占用你的本地电脑资源
- 任何时间、任何地点都能执行
- 执行环境干净一致，避免个人电脑环境差异

---

### 🟢 执行步骤（Steps）

#### **Step 1：检出代码**
```yaml
- uses: actions/checkout@v4
```
作用：把你推送的代码下载到云服务器上。

---

#### **Step 2：安装 Java 环境**
```yaml
- uses: actions/setup-java@v4
  with:
    java-version: '17'
```
作用：在云服务器上安装 Java 17（Shopizer 项目需要）。

---

#### **Step 3：给 Maven 执行权限**
```yaml
- run: chmod +x ./mvnw
```
作用：允许 Maven 封装脚本运行（Unix/Linux 权限设置）。

---

#### **Step 4：构建并运行测试**
```yaml
- run: ./mvnw -B -pl sm-core -am -Dtest=DataUtilsTest -DfailIfNoTests=false test
```

这是**核心步骤**，让我们详细拆解：

| 命令部分 | 作用 |
|--------|------|
| `./mvnw` | 调用 Maven 构建工具 |
| `-B` | 批量模式（no interactive，不需要用户输入） |
| `-pl sm-core` | 只构建 sm-core 模块（而不是全部模块） |
| `-am` | auto-make，自动构建 sm-core 依赖的其他模块 |
| `-Dtest=DataUtilsTest` | 只运行 DataUtilsTest 这个测试类 |
| `-DfailIfNoTests=false` | 允许某些模块没有测试类（防止报错） |
| `test` | 执行 test 生命周期（编译、测试、报告） |

---

## 四、这个 CI 的整体工作流程（时间线）

```
时刻 0 秒：你运行 git push
    ↓
时刻 1 秒：代码到达 GitHub
    ↓
时刻 2 秒：GitHub Actions 检测到 push 事件
    ↓
时刻 3 秒：自动启动 workflow（购买云服务器，启动 ubuntu）
    ↓
时刻 5 秒：检出代码（git clone）
    ↓
时刻 8 秒：安装 Java 17
    ↓
时刻 15 秒：运行 Maven 构建和测试
    ├─ 编译 sm-core 和依赖模块
    ├─ 运行 DataUtilsTest 的 9 个测试
    └─ 生成测试报告
    ↓
时刻 120 秒（2 分钟）：工作流完成，云服务器销毁
```

---

## 五、CI 的结果展示在哪里？

### 在 GitHub 网页上查看：
1. 打开你的仓库网页
2. 点击顶部导航栏的 **"Actions"** 标签
3. 你会看到所有历史的工作流运行
4. 每一行显示：
   - ✅ 或 ❌（成功或失败）
   - 提交消息
   - 分支名
   - 执行时间
   - 运行耗时

### 点进去可以看到：
```
build-test Job
├─ Set up job
├─ Run actions/checkout@v4  ✅
├─ Run actions/setup-java@v4  ✅
├─ Run chmod +x ./mvnw  ✅
├─ Build and run targeted tests  ✅
│  └─ Tests run: 9, Failures: 0, Errors: 0
└─ Complete job
```

---

## 六、这个 CI 有什么用？为什么要部署它？

### 用途 1：**自动化测试**
- 每次推送都自动跑测试，不用手动 `mvn test`
- 确保没有人不小心推送破损的代码

### 用途 2：**问题早发现**
- 如果新推送的代码导致测试失败，立即反馈（红色 ❌）
- 不用等到别人报告 bug 才知道有问题
- 节省大量调试时间

### 用途 3：**团队协作**
- 其他开发者可以看到码库当前的健康状态
- Pull Request 时自动运行 CI，防止不合格的代码合并

### 用途 4：**持续交付准备**
- CI 通过后，代码已经过自动验证
- 下一步可以自动部署到测试服务器或生产环境（CD 部分）
- 加速产品交付周期

### 用途 5：**文档和审计**
- 留下完整的构建历史
- 追踪何时哪个提交导致问题
- 便于后续排查故障

---

## 七、你这个 CI 的"三大问题挑战"及解决

### 问题 1：依赖无法解析（早期失败）
**表现**：`Could not resolve dependencies ... Not authorized`

**原因**：Maven 试图从远程仓库下载 sm-core-model，但权限不足

**解决**：加了 `-am`（auto-make）参数，让 Maven 用本地模块而不是远程仓库

```bash
-am  # 自动构建依赖，确保本地模块优先
```

---

### 问题 2：模块没有测试（第二次失败）
**表现**：`No tests were executed! (Set -DfailIfNoTests=false to ignore this error.)`

**原因**：sm-core-model 等模块没有名叫 DataUtilsTest 的测试，Surefire 插件就报错

**解决**：加了 `-DfailIfNoTests=false`，允许某些模块找不到匹配的测试

```bash
-DfailIfNoTests=false  # 允许模块无测试，不报错
```

---

### 问题 3：GitHub 缓存同步延迟
**表现**：推送新代码后，Actions 仍然运行旧 workflow

**原因**：GitHub 的 workflow 缓存需要时间同步

**解决**：
- 多推送几次
- 或在 workflow 文件中加注释：`# v2.0.2` 强制识别为新文件

---

## 八、从你的角度看，整个流程的价值

### 之前（无 CI）：
```
我改代码 
  ↓
我本地运行 mvn test
  ↓
我看到测试结果
  ↓
（其他人不知道我的代码是否通过测试）
```

### 现在（有 CI）：
```
我改代码 → 我 git push
  ↓
GitHub Actions 自动接管
  ↓
云服务器自动构建、测试
  ↓
结果在 GitHub 网页上显示（✅ 或 ❌）
  ↓
我的团队、老师、代码审查者能立即看到
  ↓
如果有问题，我能立即知道并修复
```

---

## 九、你的 CI 当前配置总结

| 配置项 | 当前值 | 说明 |
|-------|-------|------|
| 触发事件 | Push 任何分支 | 代码一上传就运行 |
| 运行环境 | Ubuntu 最新版 | 免费的云服务器 |
| Java 版本 | 17 | 与 Shopizer 3.2.5 兼容 |
| 测试目标 | sm-core 模块 + DataUtilsTest | 快速反馈（~2 分钟） |
| 测试数量 | 9 个测试 | 小而精的验证集 |
| 预期结果 | 9 run, 0 failures | 绿色通过 |

---

## 十、下一步可以做什么？（可选扩展）

### 🔧 优化建议：
1. **运行更全面的测试**
   ```bash
   ./mvnw -B test  # 运行所有模块的所有测试
   ```

2. **生成测试覆盖率报告**
   ```bash
   ./mvnw -B test jacoco:report
   ```

3. **只在 Pull Request 时运行**
   ```yaml
   on:
     pull_request:
       branches: main
   ```

4. **失败时发送通知**
   - Email 通知
   - Slack 通知
   - Webhook 到其他平台

5. **部署到测试服务器**
   ```bash
   ./mvnw -B clean deploy
   ```

---

## 总结

**GitHub Actions CI 就是一个"7×24 小时值班的质量检查官"**：

- 🎯 **目的**：自动验证每次代码推送的质量
- 🤖 **工作方式**：代码一上传就自动构建、测试、生成报告
- ✅ **你的当前成果**：Shopizer 项目现在有了自动化质量保障
- 📊 **可见性**：所有人都能在 GitHub Actions 页面看到当前状态
- 🚀 **价值**：节省时间、早发现问题、提高代码质量

这对于个人项目是学习，对于团队项目是标配，对于商业项目是必装。
