# Shopizer Deployment Guide / Shopizer 部署指南

> Complete guide for building, testing, and deploying the Shopizer e-commerce platform  
> Shopizer 电子商务平台的完整构建、测试和部署指南

---

## Table of Contents / 目录

- [Quick Start / 快速开始](#quick-start--快速开始)
- [Prerequisites / 前置要求](#prerequisites--前置要求)
- [Installation / 安装](#installation--安装)
- [Building the Project / 构建项目](#building-the-project--构建项目)
- [Running the Application / 运行应用](#running-the-application--运行应用)
- [Testing / 测试](#testing--测试)
- [Deployment Options / 部署选项](#deployment-options--部署选项)
- [Configuration / 配置](#configuration--配置)
- [Troubleshooting / 故障排除](#troubleshooting--故障排除)

---

## Quick Start / 快速开始

**English:** Get Shopizer running in 5 minutes using Docker:

```bash
# Pull and run the official Docker image
docker run -p 8080:8080 shopizerecomm/shopizer:latest

# Access the API documentation
open http://localhost:8080/swagger-ui.html
```

**中文:** 使用 Docker 在 5 分钟内运行 Shopizer：

```bash
# 拉取并运行官方 Docker 镜像
docker run -p 8080:8080 shopizerecomm/shopizer:latest

# 访问 API 文档
open http://localhost:8080/swagger-ui.html
```

---

## Prerequisites / 前置要求

### Required Software / 必需软件

| Software | Version | Download |
|----------|---------|----------|
| **Java JDK** | 11 or 17 | https://adoptium.net/ |
| **Maven** | 3.6+ (optional) | https://maven.apache.org/ |
| **Git** | Latest | https://git-scm.com/ |

### Optional Software / 可选软件

| Software | Purpose | Download |
|----------|---------|----------|
| **Docker** | Container deployment | https://www.docker.com/ |
| **MySQL** | Production database | https://www.mysql.com/ |
| **PostgreSQL** | Production database | https://www.postgresql.org/ |

### System Requirements / 系统要求

**English:**
- **Memory:** Minimum 4GB RAM (8GB recommended)
- **Disk Space:** 2GB free space
- **OS:** Windows, macOS, or Linux
- **Network:** Internet connection for dependencies

**中文:**
- **内存:** 最少 4GB RAM（推荐 8GB）
- **磁盘空间:** 2GB 可用空间
- **操作系统:** Windows、macOS 或 Linux
- **网络:** 需要互联网连接以下载依赖

---

## Installation / 安装

### Step 1: Install Java / 安装 Java

**English:**

1. Download and install JDK 11 or 17 from https://adoptium.net/
2. Verify installation:

```bash
java -version
# Expected output: openjdk version "11.x.x" or "17.x.x"

javac -version
# Expected output: javac 11.x.x or 17.x.x
```

3. Set `JAVA_HOME` environment variable:

**macOS/Linux:**
```bash
export JAVA_HOME=$(/usr/libexec/java_home)
echo $JAVA_HOME
```

**Windows:**
```cmd
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-17.x.x"
```

**中文:**

1. 从 https://adoptium.net/ 下载并安装 JDK 11 或 17
2. 验证安装:

```bash
java -version
# 预期输出: openjdk version "11.x.x" 或 "17.x.x"

javac -version
# 预期输出: javac 11.x.x 或 17.x.x
```

3. 设置 `JAVA_HOME` 环境变量:

**macOS/Linux:**
```bash
export JAVA_HOME=$(/usr/libexec/java_home)
echo $JAVA_HOME
```

**Windows:**
```cmd
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-17.x.x"
```

### Step 2: Clone Repository / 克隆仓库

**English:**

```bash
# Clone the repository
git clone https://github.com/[your-username]/shopizerForTest.git

# Navigate to project directory
cd shopizerForTest

# Verify project structure
ls -la
# You should see: pom.xml, sm-core/, sm-shop/, etc.
```

**中文:**

```bash
# 克隆仓库
git clone https://github.com/[your-username]/shopizerForTest.git

# 导航到项目目录
cd shopizerForTest

# 验证项目结构
ls -la
# 您应该看到: pom.xml, sm-core/, sm-shop/ 等
```

### Step 3: Verify Maven / 验证 Maven

**English:**

The project includes Maven Wrapper (`mvnw`), so Maven installation is optional.

```bash
# Using Maven Wrapper (recommended)
./mvnw --version

# Or install Maven system-wide
brew install maven  # macOS
sudo apt install maven  # Ubuntu/Debian
choco install maven  # Windows (with Chocolatey)

# Verify Maven
mvn --version
```

**中文:**

项目包含 Maven 包装器（`mvnw`），因此 Maven 安装是可选的。

```bash
# 使用 Maven 包装器（推荐）
./mvnw --version

# 或在系统范围内安装 Maven
brew install maven  # macOS
sudo apt install maven  # Ubuntu/Debian
choco install maven  # Windows（使用 Chocolatey）

# 验证 Maven
mvn --version
```

---

## Building the Project / 构建项目

### Full Build / 完整构建

**English:**

Build the entire multi-module project:

```bash
# Clean and build all modules
./mvnw clean install

# This will:
# 1. Download all dependencies (first time only)
# 2. Compile all Java source files
# 3. Run all tests
# 4. Package into JAR files
# 5. Install to local Maven repository

# Expected time: 5-10 minutes (first build)
# Expected output: BUILD SUCCESS
```

**Build output locations:**
- `sm-core/target/sm-core-3.2.5.jar`
- `sm-core-model/target/sm-core-model-3.2.5.jar`
- `sm-core-modules/target/sm-core-modules-3.2.5.jar`
- `sm-shop-model/target/sm-shop-model-3.2.5.jar`
- `sm-shop/target/sm-shop-3.2.5.jar` ← **Main application JAR**

**中文:**

构建整个多模块项目:

```bash
# 清理并构建所有模块
./mvnw clean install

# 这将:
# 1. 下载所有依赖项（仅第一次）
# 2. 编译所有 Java 源文件
# 3. 运行所有测试
# 4. 打包成 JAR 文件
# 5. 安装到本地 Maven 仓库

# 预期时间: 5-10 分钟（首次构建）
# 预期输出: BUILD SUCCESS
```

**构建输出位置:**
- `sm-core/target/sm-core-3.2.5.jar`
- `sm-core-model/target/sm-core-model-3.2.5.jar`
- `sm-core-modules/target/sm-core-modules-3.2.5.jar`
- `sm-shop-model/target/sm-shop-model-3.2.5.jar`
- `sm-shop/target/sm-shop-3.2.5.jar` ← **主应用程序 JAR**

### Build Without Tests / 跳过测试构建

**English:**

If you want to build quickly without running tests:

```bash
./mvnw clean install -DskipTests

# Much faster: ~2-3 minutes
```

**中文:**

如果您想快速构建而不运行测试:

```bash
./mvnw clean install -DskipTests

# 更快: 约 2-3 分钟
```

### Build Individual Modules / 构建单个模块

**English:**

Build only specific modules:

```bash
# Build core module only
cd sm-core
./mvnw clean install

# Build shop module only
cd sm-shop
./mvnw clean install

# Build with dependency modules
cd sm-shop
./mvnw clean install -pl . -am
```

**中文:**

仅构建特定模块:

```bash
# 仅构建核心模块
cd sm-core
./mvnw clean install

# 仅构建 shop 模块
cd sm-shop
./mvnw clean install

# 构建包含依赖模块
cd sm-shop
./mvnw clean install -pl . -am
```

---

## Running the Application / 运行应用

### Method 1: Maven Spring Boot Plugin (Development) / 方法 1: Maven Spring Boot 插件（开发）

**English:**

Best for development with hot reload:

```bash
cd sm-shop
./mvnw spring-boot:run

# Application starts on http://localhost:8080
# Press Ctrl+C to stop
```

**Access points:**
- **API Documentation:** http://localhost:8080/swagger-ui.html
- **Health Check:** http://localhost:8080/actuator/health
- **API Base:** http://localhost:8080/api/v1

**中文:**

最适合支持热重载的开发:

```bash
cd sm-shop
./mvnw spring-boot:run

# 应用程序在 http://localhost:8080 启动
# 按 Ctrl+C 停止
```

**访问点:**
- **API 文档:** http://localhost:8080/swagger-ui.html
- **健康检查:** http://localhost:8080/actuator/health
- **API 基础地址:** http://localhost:8080/api/v1

### Method 2: Executable JAR (Production-like) / 方法 2: 可执行 JAR（类生产环境）

**English:**

```bash
# Build the JAR
cd sm-shop
./mvnw clean package

# Run the JAR
java -jar target/sm-shop-3.2.5.jar

# With custom port
java -jar -Dserver.port=9090 target/sm-shop-3.2.5.jar

# With specific profile
java -jar -Dspring.profiles.active=mysql target/sm-shop-3.2.5.jar

# In background (Unix/macOS)
nohup java -jar target/sm-shop-3.2.5.jar > shopizer.log 2>&1 &

# Check process
ps aux | grep shopizer
```

**中文:**

```bash
# 构建 JAR
cd sm-shop
./mvnw clean package

# 运行 JAR
java -jar target/sm-shop-3.2.5.jar

# 使用自定义端口
java -jar -Dserver.port=9090 target/sm-shop-3.2.5.jar

# 使用特定配置文件
java -jar -Dspring.profiles.active=mysql target/sm-shop-3.2.5.jar

# 后台运行（Unix/macOS）
nohup java -jar target/sm-shop-3.2.5.jar > shopizer.log 2>&1 &

# 检查进程
ps aux | grep shopizer
```

### Method 3: Docker Container / 方法 3: Docker 容器

**English:**

**Option A: Official Docker Image**

```bash
# Pull and run latest version
docker run -p 8080:8080 shopizerecomm/shopizer:latest

# Run with custom environment
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=mysql \
  -e DB_HOST=mysql-server \
  shopizerecomm/shopizer:latest

# Run in detached mode
docker run -d -p 8080:8080 --name shopizer shopizerecomm/shopizer:latest

# View logs
docker logs -f shopizer

# Stop container
docker stop shopizer
```

**Option B: Build Custom Docker Image**

```bash
# Navigate to sm-shop
cd sm-shop

# Build Docker image
docker build -t shopizer-custom:latest .

# Run custom image
docker run -p 8080:8080 shopizer-custom:latest

# Tag and push to registry
docker tag shopizer-custom:latest your-registry/shopizer:v1.0
docker push your-registry/shopizer:v1.0
```

**中文:**

**选项 A: 官方 Docker 镜像**

```bash
# 拉取并运行最新版本
docker run -p 8080:8080 shopizerecomm/shopizer:latest

# 使用自定义环境运行
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=mysql \
  -e DB_HOST=mysql-server \
  shopizerecomm/shopizer:latest

# 在分离模式下运行
docker run -d -p 8080:8080 --name shopizer shopizerecomm/shopizer:latest

# 查看日志
docker logs -f shopizer

# 停止容器
docker stop shopizer
```

**选项 B: 构建自定义 Docker 镜像**

```bash
# 导航到 sm-shop
cd sm-shop

# 构建 Docker 镜像
docker build -t shopizer-custom:latest .

# 运行自定义镜像
docker run -p 8080:8080 shopizer-custom:latest

# 标记并推送到注册表
docker tag shopizer-custom:latest your-registry/shopizer:v1.0
docker push your-registry/shopizer:v1.0
```

### Method 4: Docker Compose (Complete Stack) / 方法 4: Docker Compose（完整堆栈）

**English:**

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: shopizer
      MYSQL_USER: shopizer
      MYSQL_PASSWORD: shopizer
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  shopizer:
    image: shopizerecomm/shopizer:latest
    depends_on:
      mysql:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: mysql
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: shopizer
      DB_USER: shopizer
      DB_PASSWORD: shopizer
    ports:
      - "8080:8080"
    volumes:
      - shopizer_data:/data

volumes:
  mysql_data:
  shopizer_data:
```

**Run the stack:**

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

**中文:**

创建 `docker-compose.yml`:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: shopizer
      MYSQL_USER: shopizer
      MYSQL_PASSWORD: shopizer
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  shopizer:
    image: shopizerecomm/shopizer:latest
    depends_on:
      mysql:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: mysql
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: shopizer
      DB_USER: shopizer
      DB_PASSWORD: shopizer
    ports:
      - "8080:8080"
    volumes:
      - shopizer_data:/data

volumes:
  mysql_data:
  shopizer_data:
```

**运行堆栈:**

```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止所有服务
docker-compose down

# 停止并删除卷
docker-compose down -v
```

---

## Testing / 测试

### Run All Tests / 运行所有测试

**English:**

```bash
# From project root
./mvnw clean test

# Run tests for specific module
cd sm-shop
./mvnw test

# Parallel test execution
./mvnw -T 4 test  # Use 4 threads
```

**Test output:**
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.salesmanager.test.shop.integration.customer.CustomerRegistrationIntegrationTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 150, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

**中文:**

```bash
# 从项目根目录
./mvnw clean test

# 运行特定模块的测试
cd sm-shop
./mvnw test

# 并行测试执行
./mvnw -T 4 test  # 使用 4 个线程
```

### Run Specific Test Classes / 运行特定测试类

**English:**

```bash
# Run single test class
./mvnw test -Dtest=CustomerRegistrationIntegrationTest

# Run multiple test classes
./mvnw test -Dtest=CustomerRegistrationIntegrationTest,ShoppingCartAPIIntegrationTest

# Run tests matching pattern
./mvnw test -Dtest=*IntegrationTest

# Run specific test method
./mvnw test -Dtest=CustomerRegistrationIntegrationTest#registerCustomer
```

**中文:**

```bash
# 运行单个测试类
./mvnw test -Dtest=CustomerRegistrationIntegrationTest

# 运行多个测试类
./mvnw test -Dtest=CustomerRegistrationIntegrationTest,ShoppingCartAPIIntegrationTest

# 运行匹配模式的测试
./mvnw test -Dtest=*IntegrationTest

# 运行特定测试方法
./mvnw test -Dtest=CustomerRegistrationIntegrationTest#registerCustomer
```

### Test Coverage Report / 测试覆盖率报告

**English:**

```bash
# Generate JaCoCo coverage report
./mvnw clean test jacoco:report

# Open coverage report
open sm-shop/target/site/jacoco/index.html  # macOS
xdg-open sm-shop/target/site/jacoco/index.html  # Linux
start sm-shop/target/site/jacoco/index.html  # Windows
```

**Coverage report includes:**
- Line coverage percentage
- Branch coverage percentage
- Class and method coverage
- Detailed source code view with coverage highlighting

**中文:**

```bash
# 生成 JaCoCo 覆盖率报告
./mvnw clean test jacoco:report

# 打开覆盖率报告
open sm-shop/target/site/jacoco/index.html  # macOS
xdg-open sm-shop/target/site/jacoco/index.html  # Linux
start sm-shop/target/site/jacoco/index.html  # Windows
```

**覆盖率报告包括:**
- 行覆盖率百分比
- 分支覆盖率百分比
- 类和方法覆盖率
- 带覆盖率突出显示的详细源代码视图

### Integration Tests / 集成测试

**English:**

```bash
# Run integration tests only
./mvnw verify -P integration-tests

# Run integration tests with specific profile
./mvnw verify -P integration-tests -Dspring.profiles.active=test
```

**中文:**

```bash
# 仅运行集成测试
./mvnw verify -P integration-tests

# 使用特定配置文件运行集成测试
./mvnw verify -P integration-tests -Dspring.profiles.active=test
```

---

## Deployment Options / 部署选项

### Local Development / 本地开发

**English:**

Best for development and testing:

```bash
cd sm-shop
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

**Configuration:**
- Uses H2 in-memory database (no setup required)
- Data is lost when application stops
- Fast startup and teardown
- Ideal for testing and development

**中文:**

最适合开发和测试:

```bash
cd sm-shop
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

**配置:**
- 使用 H2 内存数据库（无需设置）
- 应用程序停止时数据丢失
- 快速启动和关闭
- 适合测试和开发

### Production with MySQL / 使用 MySQL 的生产环境

**English:**

**Step 1: Install and Configure MySQL**

```bash
# Install MySQL (Ubuntu/Debian)
sudo apt update
sudo apt install mysql-server

# Install MySQL (macOS)
brew install mysql

# Start MySQL
sudo systemctl start mysql  # Linux
brew services start mysql  # macOS

# Secure installation
sudo mysql_secure_installation
```

**Step 2: Create Database**

```sql
-- Login to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE shopizer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'shopizer'@'localhost' IDENTIFIED BY 'your_secure_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON shopizer.* TO 'shopizer'@'localhost';
FLUSH PRIVILEGES;

-- Verify
SHOW DATABASES;
EXIT;
```

**Step 3: Configure Shopizer**

Edit `sm-shop/src/main/resources/application.properties`:

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/shopizer?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.datasource.username=shopizer
spring.datasource.password=your_secure_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Connection pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

**Step 4: Run with MySQL Profile**

```bash
cd sm-shop
./mvnw clean package
java -jar -Dspring.profiles.active=mysql target/sm-shop-3.2.5.jar
```

**中文:**

**步骤 1: 安装和配置 MySQL**

```bash
# 安装 MySQL（Ubuntu/Debian）
sudo apt update
sudo apt install mysql-server

# 安装 MySQL（macOS）
brew install mysql

# 启动 MySQL
sudo systemctl start mysql  # Linux
brew services start mysql  # macOS

# 安全安装
sudo mysql_secure_installation
```

**步骤 2: 创建数据库**

```sql
-- 登录 MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE shopizer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'shopizer'@'localhost' IDENTIFIED BY 'your_secure_password';

-- 授予权限
GRANT ALL PRIVILEGES ON shopizer.* TO 'shopizer'@'localhost';
FLUSH PRIVILEGES;

-- 验证
SHOW DATABASES;
EXIT;
```

**步骤 3: 配置 Shopizer**

编辑 `sm-shop/src/main/resources/application.properties`:

```properties
# MySQL 配置
spring.datasource.url=jdbc:mysql://localhost:3306/shopizer?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.datasource.username=shopizer
spring.datasource.password=your_secure_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA 配置
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# 连接池
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

**步骤 4: 使用 MySQL 配置文件运行**

```bash
cd sm-shop
./mvnw clean package
java -jar -Dspring.profiles.active=mysql target/sm-shop-3.2.5.jar
```

### Cloud Deployment (AWS, GCP, Azure) / 云部署（AWS、GCP、Azure）

**English:**

**AWS Deployment:**

```bash
# Build Docker image
docker build -t shopizer:latest sm-shop/

# Tag for ECR
docker tag shopizer:latest AWS_ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/shopizer:latest

# Push to ECR
aws ecr get-login-password --region REGION | docker login --username AWS --password-stdin AWS_ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com
docker push AWS_ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/shopizer:latest

# Deploy to ECS or EKS
# (Configure task definition or Kubernetes deployment)
```

**GCP Deployment:**

```bash
# Build and push to Container Registry
gcloud builds submit --tag gcr.io/PROJECT_ID/shopizer sm-shop/

# Deploy to Cloud Run
gcloud run deploy shopizer \
  --image gcr.io/PROJECT_ID/shopizer \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

**Azure Deployment:**

```bash
# Build and push to Container Registry
az acr build --registry YOUR_REGISTRY --image shopizer:latest sm-shop/

# Deploy to Azure Container Instances
az container create \
  --resource-group YOUR_RESOURCE_GROUP \
  --name shopizer \
  --image YOUR_REGISTRY.azurecr.io/shopizer:latest \
  --dns-name-label shopizer-app \
  --ports 8080
```

**中文:**

**AWS 部署:**

```bash
# 构建 Docker 镜像
docker build -t shopizer:latest sm-shop/

# 为 ECR 打标签
docker tag shopizer:latest AWS_ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/shopizer:latest

# 推送到 ECR
aws ecr get-login-password --region REGION | docker login --username AWS --password-stdin AWS_ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com
docker push AWS_ACCOUNT_ID.dkr.ecr.REGION.amazonaws.com/shopizer:latest

# 部署到 ECS 或 EKS
# （配置任务定义或 Kubernetes 部署）
```

**GCP 部署:**

```bash
# 构建并推送到 Container Registry
gcloud builds submit --tag gcr.io/PROJECT_ID/shopizer sm-shop/

# 部署到 Cloud Run
gcloud run deploy shopizer \
  --image gcr.io/PROJECT_ID/shopizer \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

**Azure 部署:**

```bash
# 构建并推送到 Container Registry
az acr build --registry YOUR_REGISTRY --image shopizer:latest sm-shop/

# 部署到 Azure Container Instances
az container create \
  --resource-group YOUR_RESOURCE_GROUP \
  --name shopizer \
  --image YOUR_REGISTRY.azurecr.io/shopizer:latest \
  --dns-name-label shopizer-app \
  --ports 8080
```

---

## Configuration / 配置

### Application Properties / 应用程序属性

**English:**

Key configuration files:

- `sm-shop/src/main/resources/application.properties` - Main configuration
- `sm-shop/src/main/resources/shopizer-properties.properties` - Shopizer-specific settings
- `sm-core/src/main/resources/profiles/` - Profile-specific configurations

**Common configurations:**

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# Database Configuration
spring.datasource.url=jdbc:h2:mem:shopizer
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Logging
logging.level.com.salesmanager=DEBUG
logging.level.org.springframework=INFO

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Email Configuration
config.cms.mail.smtp.host=smtp.gmail.com
config.cms.mail.smtp.port=587
config.cms.mail.smtp.auth=true
config.cms.mail.username=your-email@gmail.com
config.cms.mail.password=your-app-password
```

**中文:**

关键配置文件:

- `sm-shop/src/main/resources/application.properties` - 主配置
- `sm-shop/src/main/resources/shopizer-properties.properties` - Shopizer 特定设置
- `sm-core/src/main/resources/profiles/` - 特定于配置文件的配置

**常见配置:**

```properties
# 服务器配置
server.port=8080
server.servlet.context-path=/

# 数据库配置
spring.datasource.url=jdbc:h2:mem:shopizer
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# 日志记录
logging.level.com.salesmanager=DEBUG
logging.level.org.springframework=INFO

# 文件上传
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# 电子邮件配置
config.cms.mail.smtp.host=smtp.gmail.com
config.cms.mail.smtp.port=587
config.cms.mail.smtp.auth=true
config.cms.mail.username=your-email@gmail.com
config.cms.mail.password=your-app-password
```

### Environment Variables / 环境变量

**English:**

Override properties using environment variables:

```bash
# Database
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=shopizer
export DB_USER=shopizer
export DB_PASSWORD=your_password

# Server
export SERVER_PORT=8080

# Profile
export SPRING_PROFILES_ACTIVE=mysql

# Run application
java -jar sm-shop/target/sm-shop-3.2.5.jar
```

**Docker environment variables:**

```bash
docker run -p 8080:8080 \
  -e DB_HOST=mysql-server \
  -e DB_PORT=3306 \
  -e DB_NAME=shopizer \
  -e DB_USER=shopizer \
  -e DB_PASSWORD=password \
  -e SPRING_PROFILES_ACTIVE=mysql \
  shopizerecomm/shopizer:latest
```

**中文:**

使用环境变量覆盖属性:

```bash
# 数据库
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=shopizer
export DB_USER=shopizer
export DB_PASSWORD=your_password

# 服务器
export SERVER_PORT=8080

# 配置文件
export SPRING_PROFILES_ACTIVE=mysql

# 运行应用程序
java -jar sm-shop/target/sm-shop-3.2.5.jar
```

**Docker 环境变量:**

```bash
docker run -p 8080:8080 \
  -e DB_HOST=mysql-server \
  -e DB_PORT=3306 \
  -e DB_NAME=shopizer \
  -e DB_USER=shopizer \
  -e DB_PASSWORD=password \
  -e SPRING_PROFILES_ACTIVE=mysql \
  shopizerecomm/shopizer:latest
```

---

## Troubleshooting / 故障排除

### Common Issues / 常见问题

#### 1. Java Version Error / Java 版本错误

**English:**

**Error:**
```
[ERROR] Failed to execute goal: Source option 11 is no longer supported. Use 11 or later.
```

**Solution:**
```bash
# Check Java version
java -version

# Ensure Java 11+ is installed
# Update JAVA_HOME if needed
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
```

**中文:**

**错误:**
```
[ERROR] Failed to execute goal: Source option 11 is no longer supported. Use 11 or later.
```

**解决方案:**
```bash
# 检查 Java 版本
java -version

# 确保安装了 Java 11+
# 如需要更新 JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
```

#### 2. Port Already in Use / 端口已被占用

**English:**

**Error:**
```
Web server failed to start. Port 8080 was already in use.
```

**Solution:**

```bash
# Find process using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill the process
kill -9 PID  # macOS/Linux
taskkill /PID PID /F  # Windows

# Or run on different port
java -jar -Dserver.port=9090 target/sm-shop-3.2.5.jar
```

**中文:**

**错误:**
```
Web server failed to start. Port 8080 was already in use.
```

**解决方案:**

```bash
# 查找使用 8080 端口的进程
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# 终止进程
kill -9 PID  # macOS/Linux
taskkill /PID PID /F  # Windows

# 或在不同端口运行
java -jar -Dserver.port=9090 target/sm-shop-3.2.5.jar
```

#### 3. Database Connection Error / 数据库连接错误

**English:**

**Error:**
```
Unable to open JDBC Connection for DDL execution
```

**Solution:**

1. Check database is running:
```bash
# MySQL
sudo systemctl status mysql  # Linux
brew services list  # macOS

# Start if not running
sudo systemctl start mysql  # Linux
brew services start mysql  # macOS
```

2. Verify connection details in `application.properties`
3. Test database connection:
```bash
mysql -h localhost -u shopizer -p shopizer
```

4. Check firewall settings

**中文:**

**错误:**
```
Unable to open JDBC Connection for DDL execution
```

**解决方案:**

1. 检查数据库是否正在运行:
```bash
# MySQL
sudo systemctl status mysql  # Linux
brew services list  # macOS

# 如果未运行则启动
sudo systemctl start mysql  # Linux
brew services start mysql  # macOS
```

2. 验证 `application.properties` 中的连接详细信息
3. 测试数据库连接:
```bash
mysql -h localhost -u shopizer -p shopizer
```

4. 检查防火墙设置

#### 4. Out of Memory Error / 内存不足错误

**English:**

**Error:**
```
java.lang.OutOfMemoryError: Java heap space
```

**Solution:**

```bash
# Increase heap size
java -Xmx2g -Xms512m -jar target/sm-shop-3.2.5.jar

# For Maven
export MAVEN_OPTS="-Xmx2g -Xms512m"
./mvnw clean install

# For Docker
docker run -p 8080:8080 -m 2g shopizerecomm/shopizer:latest
```

**中文:**

**错误:**
```
java.lang.OutOfMemoryError: Java heap space
```

**解决方案:**

```bash
# 增加堆大小
java -Xmx2g -Xms512m -jar target/sm-shop-3.2.5.jar

# 对于 Maven
export MAVEN_OPTS="-Xmx2g -Xms512m"
./mvnw clean install

# 对于 Docker
docker run -p 8080:8080 -m 2g shopizerecomm/shopizer:latest
```

#### 5. Test Failures / 测试失败

**English:**

**Issue:** Tests fail during build

**Solution:**

```bash
# Skip tests temporarily
./mvnw clean install -DskipTests

# Run tests with detailed output
./mvnw test -X

# Run specific failing test
./mvnw test -Dtest=FailingTestClass

# Check test reports
cat sm-shop/target/surefire-reports/*.txt
```

**中文:**

**问题:** 构建期间测试失败

**解决方案:**

```bash
# 暂时跳过测试
./mvnw clean install -DskipTests

# 使用详细输出运行测试
./mvnw test -X

# 运行特定的失败测试
./mvnw test -Dtest=FailingTestClass

# 检查测试报告
cat sm-shop/target/surefire-reports/*.txt
```

### Getting Help / 获取帮助

**English:**

- **GitHub Issues:** https://github.com/shopizer-ecommerce/shopizer/issues
- **Stack Overflow:** Tag questions with `shopizer`
- **Slack Community:** https://shopizer.slack.com
- **Documentation:** https://shopizer-ecommerce.github.io/documentation/

**中文:**

- **GitHub Issues:** https://github.com/shopizer-ecommerce/shopizer/issues
- **Stack Overflow:** 使用 `shopizer` 标签提问
- **Slack 社区:** https://shopizer.slack.com
- **文档:** https://shopizer-ecommerce.github.io/documentation/

---

## API Documentation / API 文档

**English:**

Once the application is running, access the comprehensive API documentation:

**Swagger UI:** http://localhost:8080/swagger-ui.html

**Available API endpoints:**

- `/api/v1/auth/*` - Authentication and authorization
- `/api/v1/customer/*` - Customer management
- `/api/v1/products/*` - Product catalog
- `/api/v1/cart/*` - Shopping cart operations
- `/api/v1/order/*` - Order processing
- `/api/v1/store/*` - Merchant store management
- `/api/v1/category/*` - Product categories
- `/api/v1/search/*` - Search functionality

**Example API calls:**

```bash
# Health check
curl http://localhost:8080/actuator/health

# Get store info
curl http://localhost:8080/api/v1/store/DEFAULT

# Register customer
curl -X POST http://localhost:8080/api/v1/customer/register \
  -H "Content-Type: application/json" \
  -d '{
    "emailAddress": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User",
    "storeCode": "DEFAULT"
  }'
```

**中文:**

应用程序运行后，访问全面的 API 文档:

**Swagger UI:** http://localhost:8080/swagger-ui.html

**可用的 API 端点:**

- `/api/v1/auth/*` - 身份验证和授权
- `/api/v1/customer/*` - 客户管理
- `/api/v1/products/*` - 产品目录
- `/api/v1/cart/*` - 购物车操作
- `/api/v1/order/*` - 订单处理
- `/api/v1/store/*` - 商家店铺管理
- `/api/v1/category/*` - 产品类别
- `/api/v1/search/*` - 搜索功能

**API 调用示例:**

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 获取商店信息
curl http://localhost:8080/api/v1/store/DEFAULT

# 注册客户
curl -X POST http://localhost:8080/api/v1/customer/register \
  -H "Content-Type: application/json" \
  -d '{
    "emailAddress": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User",
    "storeCode": "DEFAULT"
  }'
```

---

## Project Structure / 项目结构

**English:**

```
shopizerForTest/
├── sm-core/                    # Core business logic
│   ├── src/main/java/         # Core services and repositories
│   └── src/main/resources/    # Core configuration
├── sm-core-model/             # Domain models and entities
│   └── src/main/java/         # JPA entities
├── sm-core-modules/           # Integration modules
│   └── src/main/java/         # Payment, shipping, etc.
├── sm-shop/                   # REST API application
│   ├── src/main/java/         # Controllers, facades, security
│   ├── src/main/resources/    # Application configuration
│   └── src/test/java/         # Integration tests
├── sm-shop-model/             # API models (DTOs)
│   └── src/main/java/         # Request/response models
├── pom.xml                    # Parent Maven configuration
└── README.md                  # This file
```

**中文:**

```
shopizerForTest/
├── sm-core/                    # 核心业务逻辑
│   ├── src/main/java/         # 核心服务和仓库
│   └── src/main/resources/    # 核心配置
├── sm-core-model/             # 领域模型和实体
│   └── src/main/java/         # JPA 实体
├── sm-core-modules/           # 集成模块
│   └── src/main/java/         # 支付、物流等
├── sm-shop/                   # REST API 应用程序
│   ├── src/main/java/         # 控制器、门面、安全
│   ├── src/main/resources/    # 应用程序配置
│   └── src/test/java/         # 集成测试
├── sm-shop-model/             # API 模型（DTO）
│   └── src/main/java/         # 请求/响应模型
├── pom.xml                    # 父 Maven 配置
└── README.md                  # 本文件
```

---

## Quick Reference / 快速参考

### Essential Commands / 基本命令

| Task | Command |
|------|---------|
| Build project | `./mvnw clean install` |
| Run application | `./mvnw spring-boot:run` |
| Run tests | `./mvnw test` |
| Package JAR | `./mvnw clean package` |
| Skip tests | `./mvnw clean install -DskipTests` |
| Run JAR | `java -jar sm-shop/target/sm-shop-3.2.5.jar` |
| Docker run | `docker run -p 8080:8080 shopizerecomm/shopizer:latest` |

### Important URLs / 重要 URL

| Resource | URL |
|----------|-----|
| Application | http://localhost:8080 |
| API Documentation | http://localhost:8080/swagger-ui.html |
| Health Check | http://localhost:8080/actuator/health |
| API Base | http://localhost:8080/api/v1 |

### Default Credentials / 默认凭据

| Item | Value |
|------|-------|
| Store Code | `DEFAULT` |
| Database (H2) | In-memory, no credentials |
| Admin API Key | Check application logs on first run |

---

## License / 许可证

Apache License 2.0

---

## Support / 支持

**English:**

For questions, issues, or contributions:

- **GitHub:** https://github.com/shopizer-ecommerce/shopizer
- **Documentation:** https://shopizer-ecommerce.github.io/documentation/
- **Community:** https://shopizer.slack.com

**中文:**

对于问题、问题或贡献:

- **GitHub:** https://github.com/shopizer-ecommerce/shopizer
- **文档:** https://shopizer-ecommerce.github.io/documentation/
- **社区:** https://shopizer.slack.com

---

**Happy Coding! / 编码愉快！** 🚀
