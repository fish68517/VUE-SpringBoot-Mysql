# 气象+农资一体化服务平台 - 后端项目

## 项目概述

这是一个基于SpringBoot 3.x的智能农业服务系统后端项目。该平台通过整合实时气象数据与农资供应链管理，为农户提供极端天气预警和精准的农资推荐服务。

## 技术栈

- **Java版本**: JDK 17+
- **框架**: Spring Boot 3.2.0
- **ORM**: Spring Data JPA + Hibernate
- **数据库**: MySQL 8.0+
- **安全**: Spring Security + JWT
- **构建工具**: Maven
- **日志**: SLF4J + Logback

## 项目结构

```
src/
├── main/
│   ├── java/com/agricultural/
│   │   ├── controller/          # REST API控制器
│   │   ├── service/             # 业务逻辑服务
│   │   ├── repository/          # 数据访问层
│   │   ├── entity/              # JPA实体类
│   │   ├── dto/                 # 数据传输对象
│   │   ├── util/                # 工具类
│   │   ├── config/              # 配置类
│   │   └── WeatherAgriculturalPlatformApplication.java  # 主应用程序入口
│   └── resources/
│       └── application.yml      # 应用配置文件
└── test/
    ├── java/com/agricultural/   # 测试类
    └── resources/
        └── application-test.yml # 测试配置文件
```

## 快速开始

### 前置条件

- JDK 17或更高版本
- Maven 3.6+
- MySQL 8.0+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd SpringBoot
   ```

2. **配置数据库**
   - 创建MySQL数据库: `weather_agricultural_db`
   - 修改 `src/main/resources/application.yml` 中的数据库连接信息

3. **构建项目**
   ```bash
   mvn clean install
   ```

4. **运行应用**
   ```bash
   mvn spring-boot:run
   ```

应用将在 `http://localhost:8080` 启动

## 配置说明

### application.yml 主要配置项

- **数据库连接**: 配置MySQL连接URL、用户名和密码
- **JPA配置**: 配置Hibernate方言和SQL格式化
- **日志配置**: 配置日志级别和输出格式
- **服务器配置**: 配置应用端口和上下文路径
- **JWT配置**: 配置JWT密钥和过期时间
- **CORS配置**: 配置允许的跨域请求源

## API接口

### 健康检查

- **GET** `/api/health` - 检查应用程序健康状态

## 核心模块

### 1. 用户管理模块
- 用户注册、登录、权限管理

### 2. 气象数据模块
- 气象数据获取、存储、查询

### 3. 预警管理模块
- 极端天气预警的发布和管理

### 4. 农资管理模块
- 农资产品的增删改查

### 5. 推荐引擎模块
- 根据预警和作物信息推荐农资

### 6. 订单管理模块
- 订单的创建、支付、发货、跟踪

### 7. 数据分析模块
- 数据统计和报表生成

## 开发规范

- 所有代码使用中文注释
- 遵循RESTful API设计规范
- 使用统一的响应格式
- 实现全局异常处理
- 记录详细的操作日志

## 许可证

MIT License

## 作者

Agricultural Platform Team
