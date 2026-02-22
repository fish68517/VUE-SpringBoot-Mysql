# 系统设计文档

## 概述

"气象+农资"一体化服务平台采用前后端分离架构，后端使用SpringBoot 3.x框架，前端使用Vue框架，数据库使用MySQL。系统通过集成气象数据、农资信息和用户管理，为农户提供智能化的防灾农资推荐服务。

## 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                     前端层 (Vue)                              │
│  ┌──────────────┬──────────────┬──────────────┬──────────────┐
│  │  农户端      │  商家端      │  管理员端    │  公共模块    │
│  └──────────────┴──────────────┴──────────────┴──────────────┘
└─────────────────────────────────────────────────────────────┘
                          ↓ HTTP/REST API
┌─────────────────────────────────────────────────────────────┐
│                   后端层 (SpringBoot 3.x)                    │
│  ┌──────────────────────────────────────────────────────────┐
│  │                    控制层 (Controller)                     │
│  │  ┌────────────┬────────────┬────────────┬────────────┐   │
│  │  │ 用户管理   │ 气象管理   │ 农资管理   │ 订单管理   │   │
│  │  └────────────┴────────────┴────────────┴────────────┘   │
│  └──────────────────────────────────────────────────────────┘
│  ┌──────────────────────────────────────────────────────────┐
│  │                    业务层 (Service)                       │
│  │  ┌────────────┬────────────┬────────────┬────────────┐   │
│  │  │ 用户服务   │ 气象服务   │ 农资服务   │ 推荐引擎   │   │
│  │  └────────────┴────────────┴────────────┴────────────┘   │
│  └──────────────────────────────────────────────────────────┘
│  ┌──────────────────────────────────────────────────────────┐
│  │                    数据层 (Repository)                    │
│  │  ┌────────────┬────────────┬────────────┬────────────┐   │
│  │  │ 用户仓储   │ 气象仓储   │ 农资仓储   │ 订单仓储   │   │
│  │  └────────────┴────────────┴────────────┴────────────┘   │
│  └──────────────────────────────────────────────────────────┘
└─────────────────────────────────────────────────────────────┘
                          ↓ JDBC
┌─────────────────────────────────────────────────────────────┐
│                   数据层 (MySQL)                             │
│  ┌──────────────┬──────────────┬──────────────┬──────────────┐
│  │  用户表      │  气象表      │  农资表      │  订单表      │
│  │  作物表      │  预警表      │  推荐表      │  日志表      │
│  └──────────────┴──────────────┴──────────────┴──────────────┘
└─────────────────────────────────────────────────────────────┘
```

### 分层设计

- **表现层**：Vue前端应用，提供用户界面
- **控制层**：SpringBoot Controller，处理HTTP请求
- **业务层**：Service类，实现业务逻辑
- **数据层**：Repository接口和实现，处理数据库操作
- **数据库层**：MySQL数据库，存储所有数据

## 组件与接口设计

### 核心模块

#### 1. 用户管理模块
- **功能**：用户注册、登录、权限管理
- **主要类**：User、UserService、UserController
- **接口**：
  - POST /api/users/register - 用户注册
  - POST /api/users/login - 用户登录
  - GET /api/users/{id} - 获取用户信息
  - PUT /api/users/{id} - 更新用户信息
  - DELETE /api/users/{id} - 删除用户

#### 2. 气象数据模块
- **功能**：气象数据获取、存储、查询
- **主要类**：WeatherData、WeatherService、WeatherController
- **接口**：
  - GET /api/weather/current - 获取当前气象数据
  - GET /api/weather/forecast - 获取气象预报
  - GET /api/weather/history - 获取历史气象数据

#### 3. 预警管理模块
- **功能**：极端天气预警的发布和管理
- **主要类**：Warning、WarningService、WarningController
- **接口**：
  - POST /api/warnings - 发布预警
  - GET /api/warnings - 获取预警列表
  - PUT /api/warnings/{id} - 更新预警
  - DELETE /api/warnings/{id} - 删除预警

#### 4. 农资管理模块
- **功能**：农资产品的增删改查
- **主要类**：AgriculturalProduct、ProductService、ProductController
- **接口**：
  - POST /api/products - 添加农资产品
  - GET /api/products - 获取产品列表
  - PUT /api/products/{id} - 更新产品信息
  - DELETE /api/products/{id} - 删除产品

#### 5. 推荐引擎模块
- **功能**：根据预警和作物信息推荐农资
- **主要类**：Recommendation、RecommendationEngine、RecommendationController
- **接口**：
  - POST /api/recommendations - 生成推荐
  - GET /api/recommendations/{warningId} - 获取推荐列表

#### 6. 订单管理模块
- **功能**：订单的创建、支付、发货、跟踪
- **主要类**：Order、OrderService、OrderController
- **接口**：
  - POST /api/orders - 创建订单
  - GET /api/orders - 获取订单列表
  - PUT /api/orders/{id}/status - 更新订单状态
  - GET /api/orders/{id} - 获取订单详情

## 数据模型设计

### 用户表 (users)
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100),
  phone VARCHAR(20),
  user_type ENUM('farmer', 'merchant', 'admin') NOT NULL,
  region VARCHAR(100),
  status ENUM('active', 'inactive', 'locked') DEFAULT 'active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 气象数据表 (weather_data)
```sql
CREATE TABLE weather_data (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  region VARCHAR(100) NOT NULL,
  temperature DECIMAL(5, 2),
  humidity INT,
  precipitation DECIMAL(8, 2),
  wind_speed DECIMAL(5, 2),
  weather_condition VARCHAR(50),
  recorded_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 预警表 (warnings)
```sql
CREATE TABLE warnings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  warning_type VARCHAR(50) NOT NULL,
  region VARCHAR(100) NOT NULL,
  severity ENUM('low', 'medium', 'high', 'critical') NOT NULL,
  description TEXT,
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  status ENUM('active', 'expired', 'cancelled') DEFAULT 'active',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 农资产品表 (agricultural_products)
```sql
CREATE TABLE agricultural_products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_name VARCHAR(100) NOT NULL,
  category VARCHAR(50),
  description TEXT,
  price DECIMAL(10, 2),
  stock INT,
  merchant_id BIGINT,
  applicable_weather VARCHAR(100),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (merchant_id) REFERENCES users(id)
);
```

### 作物表 (crops)
```sql
CREATE TABLE crops (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  crop_name VARCHAR(50) NOT NULL,
  growth_stage VARCHAR(50),
  region VARCHAR(100),
  user_id BIGINT,
  planting_date DATE,
  expected_harvest_date DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 推荐表 (recommendations)
```sql
CREATE TABLE recommendations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  warning_id BIGINT,
  product_id BIGINT,
  user_id BIGINT,
  priority INT,
  reason TEXT,
  status ENUM('pending', 'accepted', 'rejected') DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (warning_id) REFERENCES warnings(id),
  FOREIGN KEY (product_id) REFERENCES agricultural_products(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 订单表 (orders)
```sql
CREATE TABLE orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_number VARCHAR(50) UNIQUE NOT NULL,
  user_id BIGINT,
  product_id BIGINT,
  quantity INT,
  total_price DECIMAL(10, 2),
  status ENUM('pending', 'paid', 'shipped', 'delivered', 'cancelled') DEFAULT 'pending',
  payment_method VARCHAR(50),
  delivery_address TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (product_id) REFERENCES agricultural_products(id)
);
```

## 错误处理

### 异常处理策略
- 使用统一的异常处理机制
- 创建自定义异常类：BusinessException、DataAccessException
- 使用@ControllerAdvice进行全局异常处理
- 返回统一的错误响应格式

### 错误响应格式
```json
{
  "code": 400,
  "message": "错误信息描述",
  "timestamp": "2024-01-01T12:00:00Z",
  "path": "/api/endpoint"
}
```

## 测试策略

### 单元测试
- 测试Service层的业务逻辑
- 测试数据验证和转换
- 使用JUnit 5和Mockito框架

### 集成测试
- 测试Controller和Service的集成
- 测试数据库操作
- 使用@SpringBootTest注解

### 系统测试
- 测试完整的业务流程
- 验证前后端交互
- 测试数据一致性

## 技术栈

- **后端框架**：SpringBoot 3.x
- **ORM框架**：JPA/Hibernate
- **数据库**：MySQL 8.0+
- **前端框架**：Vue 3
- **构建工具**：Maven
- **版本控制**：Git
- **日志框架**：SLF4J + Logback

## 安全考虑

- 使用Spring Security进行身份验证和授权
- 密码使用BCrypt加密存储
- 实现CSRF保护
- 使用HTTPS进行数据传输
- 实现API速率限制
- 定期进行安全审计

