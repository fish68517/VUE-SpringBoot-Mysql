# 旅游管理系统 - 后端项目

## 项目概述

这是一个基于SpringBoot 3.x + MySQL的广州旅游管理系统后端项目。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 5.7+
- **ORM**: Spring Data JPA
- **构建工具**: Maven 3.6+
- **Java版本**: 17+

## 项目结构

```
SpringBoot/
├── src/
│   ├── main/
│   │   ├── java/com/tourism/
│   │   │   ├── TourismManagementApplication.java    # 主应用程序入口
│   │   │   ├── common/                              # 公共类
│   │   │   │   └── ApiResponse.java                 # 统一API响应格式
│   │   │   ├── exception/                           # 异常处理
│   │   │   │   ├── GlobalExceptionHandler.java      # 全局异常处理器
│   │   │   │   ├── BusinessException.java           # 业务异常
│   │   │   │   ├── ValidationException.java         # 验证异常
│   │   │   │   └── AuthorizationException.java      # 权限异常
│   │   │   ├── util/                                # 工具类
│   │   │   │   ├── ValidationUtil.java              # 数据验证工具
│   │   │   │   └── LoggerUtil.java                  # 日志工具
│   │   │   ├── entity/                              # 实体类
│   │   │   ├── repository/                          # 数据访问层
│   │   │   ├── service/                             # 业务逻辑层
│   │   │   └── controller/                          # 控制层
│   │   └── resources/
│   │       ├── application.yml                      # 应用配置文件
│   │       └── schema.sql                           # 数据库初始化脚本
│   └── test/                                        # 测试代码
├── pom.xml                                          # Maven配置文件
└── README.md                                        # 项目说明文档
```

## 环境配置

### 1. 数据库配置

#### 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS tourism_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 初始化数据库表

执行 `src/main/resources/schema.sql` 文件中的SQL语句来创建所有必要的表。

### 2. 应用配置

编辑 `src/main/resources/application.yml` 文件，配置数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tourism_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8mb4
    username: root          # 修改为你的MySQL用户名
    password: root          # 修改为你的MySQL密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## 构建和运行

### 1. 构建项目

```bash
mvn clean install
```

### 2. 运行应用

```bash
mvn spring-boot:run
```

或者

```bash
java -jar target/tourism-management-system-1.0.0.jar
```

### 3. 访问应用

应用启动后，可以通过以下地址访问：

- API基础URL: `http://localhost:8080/api`
- 日志文件: `logs/application.log`

## API响应格式

所有API响应都遵循统一的格式：

### 成功响应

```json
{
  "code": "0000",
  "message": "成功",
  "data": {
    // 响应数据
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 错误响应

```json
{
  "code": "ERROR_CODE",
  "message": "错误信息描述",
  "data": null,
  "timestamp": "2024-01-01T12:00:00"
}
```

## 异常处理

系统定义了以下异常类型：

- **BusinessException**: 业务异常
- **ValidationException**: 验证异常
- **AuthorizationException**: 权限异常

所有异常都会被全局异常处理器捕获并返回统一的错误响应。

## 工具类

### ValidationUtil - 数据验证工具

提供以下验证方法：
- `validateUsername()`: 验证用户名
- `validatePassword()`: 验证密码
- `validateEmail()`: 验证邮箱
- `validatePhone()`: 验证手机号
- `validateNotEmpty()`: 验证非空字符串
- `validatePositive()`: 验证正数
- `validateNonNegative()`: 验证非负数

### LoggerUtil - 日志工具

提供以下日志方法：
- `info()`: 记录信息日志
- `debug()`: 记录调试日志
- `warn()`: 记录警告日志
- `error()`: 记录错误日志
- `logOperation()`: 记录操作日志
- `logBusinessException()`: 记录业务异常
- `logSystemException()`: 记录系统异常

## 数据库表说明

系统包含以下主要数据表：

| 表名 | 说明 |
|------|------|
| users | 用户表 |
| attractions | 景点表 |
| attraction_tags | 景点标签表 |
| hotels | 酒店表 |
| hotel_rooms | 酒店房间表 |
| products | 旅游商品表 |
| orders | 订单表 |
| order_items | 订单项目表 |
| routes | 旅游路线表 |
| route_items | 路线项目表 |
| comments | 留言表 |
| announcements | 公告表 |
| favorites | 收藏表 |
| browsing_history | 浏览历史表 |

## 开发指南

### 添加新的Entity

1. 在 `src/main/java/com/tourism/entity/` 目录下创建实体类
2. 使用 `@Entity` 和 `@Table` 注解
3. 使用 `@Id` 和 `@GeneratedValue` 注解定义主键

### 添加新的Repository

1. 在 `src/main/java/com/tourism/repository/` 目录下创建Repository接口
2. 继承 `JpaRepository<Entity, ID>`

### 添加新的Service

1. 在 `src/main/java/com/tourism/service/` 目录下创建Service类
2. 使用 `@Service` 注解
3. 注入相应的Repository

### 添加新的Controller

1. 在 `src/main/java/com/tourism/controller/` 目录下创建Controller类
2. 使用 `@RestController` 和 `@RequestMapping` 注解
3. 注入相应的Service

## 日志配置

日志配置在 `application.yml` 中：

```yaml
logging:
  level:
    root: INFO
    com.tourism: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/application.log
```

## 常见问题

### 1. 数据库连接失败

检查以下内容：
- MySQL服务是否启动
- 数据库连接信息是否正确
- 数据库是否已创建

### 2. 端口被占用

修改 `application.yml` 中的 `server.port` 配置。

### 3. 表不存在

执行 `schema.sql` 文件中的SQL语句来创建表。

## 许可证

本项目为毕业设计项目。
