# 个人健康管理系统 - 后端

这是个人健康管理系统的SpringBoot 3.X后端应用，提供RESTful API接口支持用户、医师和管理员的各项功能。

## 项目概述

个人健康管理系统是一个综合的健康管理平台，支持以下核心功能：

- **用户管理**: 用户注册、登录、个人信息管理
- **健康数据管理**: 健康数据采集、查询、趋势分析
- **医师功能**: 患者档案查看、数据审核、健康建议推送
- **管理员功能**: 用户权限管理、医师管理、数据统计、审计日志

## 技术栈

- **Java 17** - 编程语言
- **SpringBoot 3.2.0** - 应用框架
- **Spring Data JPA** - 数据访问层
- **MySQL 8.0+** - 数据库
- **Maven** - 项目构建工具
- **Lombok** - 代码简化工具

## 项目结构

```
src/main/java/com/health/
├── HealthManagementApplication.java    # 应用启动类
├── config/                             # 配置类
│   ├── CorsConfig.java                # CORS跨域配置
│   └── GlobalExceptionHandler.java    # 全局异常处理
├── controller/                         # 控制层
│   ├── AuthController.java            # 认证接口
│   ├── UserController.java            # 用户接口
│   ├── HealthDataController.java      # 健康数据接口
│   ├── DoctorController.java          # 医师接口
│   ├── ConsultationController.java    # 咨询接口
│   ├── HealthAdviceController.java    # 健康建议接口
│   └── AdminController.java           # 管理员接口
├── service/                            # 业务层
│   ├── AuthService.java               # 认证服务
│   ├── UserService.java               # 用户服务
│   ├── HealthDataService.java         # 健康数据服务
│   ├── DoctorService.java             # 医师服务
│   ├── ConsultationService.java       # 咨询服务
│   ├── HealthAdviceService.java       # 健康建议服务
│   └── AdminService.java              # 管理员服务
├── repository/                         # 数据访问层
│   ├── UserRepository.java
│   ├── DoctorRepository.java
│   ├── HealthDataRepository.java
│   ├── ConsultationRepository.java
│   ├── HealthAdviceRepository.java
│   └── AuditLogRepository.java
├── entity/                             # 实体类
│   ├── User.java
│   ├── Doctor.java
│   ├── HealthData.java
│   ├── Consultation.java
│   ├── HealthAdvice.java
│   └── AuditLog.java
├── dto/                                # 数据传输对象
│   ├── LoginResponse.java
│   ├── RegisterRequest.java
│   ├── DoctorRegisterRequest.java
│   ├── UserUpdateRequest.java
│   ├── ConsultationRequest.java
│   ├── ConsultationAnswerRequest.java
│   └── HealthAdviceRequest.java
├── exception/                          # 异常类
│   └── BusinessException.java
├── util/                               # 工具类
│   ├── ValidationUtil.java            # 数据验证工具
│   └── AuditLogUtil.java              # 审计日志工具
└── resources/
    └── application.yml                # 应用配置文件

src/test/java/com/health/
├── service/                            # 服务层单元测试
├── controller/                         # 控制层集成测试
└── util/                               # 工具类测试
```

## 快速开始

### 前置条件

- Java 17 或更高版本
- Maven 3.6 或更高版本
- MySQL 8.0 或更高版本

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd SpringBoot
   ```

2. **创建数据库**
   ```bash
   mysql -u root -p
   CREATE DATABASE health_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **配置数据库连接**
   
   编辑 `src/main/resources/application.yml`，修改数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/health_management?useSSL=false&serverTimezone=UTC&characterEncoding=utf8mb4
       username: root
       password: your_password
   ```

4. **构建项目**
   ```bash
   mvn clean install
   ```

5. **运行应用**
   ```bash
   mvn spring-boot:run
   ```

   应用将在 `http://localhost:8080/api` 启动。

## 数据库初始化

### 创建表结构

应用启动时会自动创建数据库表（基于JPA配置）。如需手动创建，执行以下SQL脚本：

```sql
-- 用户表
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100),
  name VARCHAR(100),
  age INT,
  gender VARCHAR(10),
  phone VARCHAR(20),
  role VARCHAR(20) DEFAULT 'USER',
  status VARCHAR(20) DEFAULT 'ACTIVE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 医师表
CREATE TABLE doctors (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNIQUE NOT NULL,
  license_number VARCHAR(50) UNIQUE NOT NULL,
  specialization VARCHAR(100),
  hospital VARCHAR(100),
  verified BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 健康数据表
CREATE TABLE health_data (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  height DECIMAL(5,2),
  weight DECIMAL(5,2),
  blood_pressure VARCHAR(20),
  heart_rate INT,
  diet_record TEXT,
  exercise_record TEXT,
  data_type VARCHAR(50),
  recorded_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 咨询表
CREATE TABLE consultations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  doctor_id BIGINT,
  question TEXT NOT NULL,
  answer TEXT,
  status VARCHAR(20) DEFAULT 'PENDING',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  answered_at TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

-- 健康建议表
CREATE TABLE health_advice (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  doctor_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  advice_content TEXT NOT NULL,
  recommendation TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (doctor_id) REFERENCES doctors(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 审计日志表
CREATE TABLE audit_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  action VARCHAR(100),
  resource VARCHAR(100),
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  details TEXT,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 创建索引以提高查询性能
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_health_data_user_id ON health_data(user_id);
CREATE INDEX idx_health_data_recorded_at ON health_data(recorded_at);
CREATE INDEX idx_consultations_user_id ON consultations(user_id);
CREATE INDEX idx_consultations_doctor_id ON consultations(doctor_id);
CREATE INDEX idx_consultations_status ON consultations(status);
CREATE INDEX idx_health_advice_doctor_id ON health_advice(doctor_id);
CREATE INDEX idx_health_advice_user_id ON health_advice(user_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
```

### 插入初始管理员账户

```sql
INSERT INTO users (username, password, email, name, role, status) 
VALUES ('admin', 'admin123', 'admin@health.com', '系统管理员', 'ADMIN', 'ACTIVE');
```

## API 文档

### 认证接口

#### 用户注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "user123",
  "password": "password123",
  "email": "user@example.com",
  "name": "张三",
  "age": 30,
  "gender": "男",
  "phone": "13800138000"
}

Response: 200 OK
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "user123",
    "role": "USER"
  }
}
```

#### 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "user123",
  "password": "password123"
}

Response: 200 OK
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "user123",
    "role": "USER",
    "name": "张三"
  }
}
```

#### 医师注册
```
POST /api/auth/doctor/register
Content-Type: application/json

{
  "username": "doctor123",
  "password": "password123",
  "email": "doctor@example.com",
  "name": "李医生",
  "licenseNumber": "DOC123456",
  "specialization": "内科",
  "hospital": "人民医院"
}

Response: 200 OK
{
  "code": 200,
  "message": "医师注册成功",
  "data": {
    "id": 2,
    "username": "doctor123",
    "role": "DOCTOR"
  }
}
```

#### 医师登录
```
POST /api/auth/doctor/login
Content-Type: application/json

{
  "username": "doctor123",
  "password": "password123"
}

Response: 200 OK
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 2,
    "username": "doctor123",
    "role": "DOCTOR",
    "name": "李医生"
  }
}
```

#### 管理员登录
```
POST /api/auth/admin/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response: 200 OK
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 3,
    "username": "admin",
    "role": "ADMIN",
    "name": "系统管理员"
  }
}
```

#### 登出
```
POST /api/auth/logout

Response: 200 OK
{
  "code": 200,
  "message": "登出成功"
}
```

### 用户接口

#### 获取个人信息
```
GET /api/users/profile

Response: 200 OK
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "user123",
    "email": "user@example.com",
    "name": "张三",
    "age": 30,
    "gender": "男",
    "phone": "13800138000"
  }
}
```

#### 更新个人信息
```
PUT /api/users/profile
Content-Type: application/json

{
  "email": "newemail@example.com",
  "name": "张三",
  "age": 31,
  "gender": "男",
  "phone": "13800138001"
}

Response: 200 OK
{
  "code": 200,
  "message": "更新成功"
}
```

### 健康数据接口

#### 提交健康数据
```
POST /api/health-data
Content-Type: application/json

{
  "height": 175.5,
  "weight": 70.0,
  "bloodPressure": "120/80",
  "heartRate": 72,
  "dietRecord": "早餐：粥、鸡蛋",
  "exerciseRecord": "跑步30分钟",
  "dataType": "ROUTINE"
}

Response: 200 OK
{
  "code": 200,
  "message": "数据保存成功",
  "data": {
    "id": 1,
    "userId": 1,
    "height": 175.5,
    "weight": 70.0,
    "recordedAt": "2024-01-01T12:00:00"
  }
}
```

#### 获取用户健康数据
```
GET /api/health-data

Response: 200 OK
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "height": 175.5,
      "weight": 70.0,
      "bloodPressure": "120/80",
      "heartRate": 72,
      "recordedAt": "2024-01-01T12:00:00"
    }
  ]
}
```

#### 获取健康趋势
```
GET /api/health-data/trends

Response: 200 OK
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "averageWeight": 70.0,
    "averageHeartRate": 72,
    "dataCount": 10,
    "trend": "稳定"
  }
}
```

#### 按时间范围查询
```
GET /api/health-data/range?startDate=2024-01-01&endDate=2024-01-31

Response: 200 OK
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "height": 175.5,
      "weight": 70.0,
      "recordedAt": "2024-01-01T12:00:00"
    }
  ]
}
```

### 医师接口

#### 获取患者列表
```
GET /api/doctors/patients

Response: 200 OK
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "username": "user123",
      "name": "张三",
      "age": 30,
      "gender": "男"
    }
  ]
}
```

#### 获取患者档案
```
GET /api/doctors/patients/{patientId}

Response: 200 OK
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "user123",
    "name": "张三",
    "age": 30,
    "gender": "男",
    "email": "user@example.com",
    "phone": "13800138000",
    "healthData": [...],
    "consultations": [...]
  }
}
```

### 咨询接口

#### 提交咨询
```
POST /api/consultations
Content-Type: application/json

{
  "question": "最近感觉疲劳，应该怎么办？"
}

Response: 200 OK
{
  "code": 200,
  "message": "咨询提交成功",
  "data": {
    "id": 1,
    "question": "最近感觉疲劳，应该怎么办？",
    "status": "PENDING",
    "createdAt": "2024-01-01T12:00:00"
  }
}
```

#### 获取咨询列表
```
GET /api/consultations

Response: 200 OK
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "question": "最近感觉疲劳，应该怎么办？",
      "answer": "建议多休息，保证充足睡眠",
      "status": "ANSWERED",
      "createdAt": "2024-01-01T12:00:00"
    }
  ]
}
```

#### 医师回复咨询
```
PUT /api/consultations/{id}/answer
Content-Type: application/json

{
  "answer": "建议多休息，保证充足睡眠"
}

Response: 200 OK
{
  "code": 200,
  "message": "回复成功"
}
```

### 健康建议接口

#### 创建健康建议
```
POST /api/health-advice
Content-Type: application/json

{
  "userId": 1,
  "adviceContent": "根据您的健康数据，建议增加运动量",
  "recommendation": "每周运动3-5次，每次30分钟"
}

Response: 200 OK
{
  "code": 200,
  "message": "建议创建成功",
  "data": {
    "id": 1,
    "adviceContent": "根据您的健康数据，建议增加运动量",
    "recommendation": "每周运动3-5次，每次30分钟",
    "createdAt": "2024-01-01T12:00:00"
  }
}
```

#### 获取患者健康建议
```
GET /api/health-advice/patient/{patientId}

Response: 200 OK
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "adviceContent": "根据您的健康数据，建议增加运动量",
      "recommendation": "每周运动3-5次，每次30分钟",
      "createdAt": "2024-01-01T12:00:00"
    }
  ]
}
```

### 管理员接口

#### 获取用户列表
```
GET /api/admin/users

Response: 200 OK
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "username": "user123",
      "name": "张三",
      "role": "USER",
      "status": "ACTIVE"
    }
  ]
}
```

#### 修改用户权限
```
PUT /api/admin/users/{id}/role
Content-Type: application/json

{
  "role": "DOCTOR"
}

Response: 200 OK
{
  "code": 200,
  "message": "权限修改成功"
}
```

#### 禁用用户
```
PUT /api/admin/users/{id}/status
Content-Type: application/json

{
  "status": "INACTIVE"
}

Response: 200 OK
{
  "code": 200,
  "message": "用户状态更新成功"
}
```

#### 获取医师列表
```
GET /api/admin/doctors

Response: 200 OK
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "username": "doctor123",
      "name": "李医生",
      "licenseNumber": "DOC123456",
      "specialization": "内科",
      "hospital": "人民医院"
    }
  ]
}
```

#### 获取数据统计
```
GET /api/admin/statistics

Response: 200 OK
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "totalUsers": 100,
    "totalDoctors": 10,
    "totalHealthData": 500,
    "totalConsultations": 50
  }
}
```

#### 获取审计日志
```
GET /api/admin/audit-logs

Response: 200 OK
{
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "action": "登录",
      "resource": "用户认证",
      "timestamp": "2024-01-01T12:00:00",
      "details": "用户user123登录成功"
    }
  ]
}
```

## 运行测试

### 运行所有测试
```bash
mvn test
```

### 运行特定测试类
```bash
mvn test -Dtest=AuthServiceTest
```

### 运行特定测试方法
```bash
mvn test -Dtest=AuthServiceTest#testUserRegistration
```

### 生成测试覆盖率报告
```bash
mvn test jacoco:report
```

## 配置说明

### application.yml 配置项

```yaml
spring:
  application:
    name: 个人健康管理系统
  
  # 数据库配置
  datasource:
    url: jdbc:mysql://localhost:3306/health_management?useSSL=false&serverTimezone=UTC&characterEncoding=utf8mb4
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  # JPA配置
  jpa:
    hibernate:
      ddl-auto: update  # 自动更新表结构
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
  
  # Jackson配置
  jackson:
    default-property-inclusion: non_null
    serialization:
      write-dates-as-timestamps: false

# 日志配置
logging:
  level:
    root: INFO
    com.health: DEBUG
  file:
    name: logs/application.log

# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /api
```

## 常见问题

### Q: 如何修改数据库连接信息？
A: 编辑 `src/main/resources/application.yml` 文件，修改 `spring.datasource` 部分的 `url`、`username` 和 `password`。

### Q: 如何修改服务器端口？
A: 编辑 `src/main/resources/application.yml` 文件，修改 `server.port` 的值。

### Q: 如何启用SQL日志输出？
A: 编辑 `src/main/resources/application.yml` 文件，将 `spring.jpa.show-sql` 改为 `true`。

### Q: 如何处理跨域请求？
A: 系统已配置CORS支持，允许所有跨域请求。配置文件位于 `src/main/java/com/health/config/CorsConfig.java`。

### Q: 如何添加新的API端点？
A: 
1. 在 `controller` 目录创建新的Controller类
2. 在 `service` 目录创建对应的Service类
3. 在 `repository` 目录创建对应的Repository接口
4. 使用 `@RestController` 和 `@RequestMapping` 注解定义端点

## 开发指南

### 代码规范

1. **命名规范**
   - 类名使用PascalCase（如 `UserService`）
   - 方法名使用camelCase（如 `getUserById`）
   - 常量使用UPPER_SNAKE_CASE（如 `MAX_PAGE_SIZE`）

2. **注解使用**
   - 使用 `@Slf4j` 进行日志记录
   - 使用 `@Data` 简化实体类
   - 使用 `@Service`、`@Repository`、`@Controller` 标注类

3. **异常处理**
   - 使用自定义 `BusinessException` 处理业务异常
   - 在Service层抛出异常，由GlobalExceptionHandler统一处理

4. **数据验证**
   - 使用 `ValidationUtil` 进行数据验证
   - 在Controller层验证请求参数

### 添加新功能的步骤

1. 在 `entity` 目录创建实体类
2. 在 `repository` 目录创建Repository接口
3. 在 `service` 目录创建Service类
4. 在 `controller` 目录创建Controller类
5. 编写单元测试和集成测试
6. 更新API文档

## 部署

详见 [部署指南](../DEPLOYMENT.md)

## 许可证

MIT

