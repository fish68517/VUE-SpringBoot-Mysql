# 医校联动实习平台 - 后端

## 项目概述

医校联动实习平台后端采用 SpringBoot 框架，提供 RESTful API 接口，支持多学校、多医院的实习管理。

## 技术栈

- Java 1.8
- SpringBoot 2.7.14
- Spring Data JPA
- MySQL 5.7+
- Lombok
- Validation

## 项目结构

```
src/
├── main/
│   ├── java/com/medical/internship/
│   │   ├── MedicalInternshipApplication.java    # 主应用程序
│   │   ├── common/                              # 通用类
│   │   │   ├── ApiResponse.java                 # 统一响应格式
│   │   │   ├── BusinessException.java           # 业务异常
│   │   │   ├── ResourceNotFoundException.java    # 资源不存在异常
│   │   │   ├── AccessDeniedException.java       # 权限不足异常
│   │   │   └── GlobalExceptionHandler.java      # 全局异常处理
│   │   ├── controller/                          # 控制器层
│   │   ├── service/                             # 业务逻辑层
│   │   ├── repository/                          # 数据访问层
│   │   └── entity/                              # 实体类
│   └── resources/
│       └── application.yml                      # 应用配置
└── test/                                        # 测试代码
```

## 快速开始

### 1. 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+

### 2. 数据库配置

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medical_internship?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
    username: root
    password: root
```

### 3. 编译和运行

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 打包项目
mvn clean package
```

### 4. 访问应用

应用启动后，可以访问：
- 健康检查: http://localhost:8080/api/health

## API 文档

详见设计文档中的 API 接口清单。

## 代码规范

- 所有代码注释采用简体中文
- 遵循 Java 命名规范
- 使用 Lombok 简化代码
- 使用 JSR-303 进行参数验证

## 异常处理

系统统一使用 `ApiResponse` 格式返回响应，异常通过 `GlobalExceptionHandler` 统一处理。

## 许可证

MIT
