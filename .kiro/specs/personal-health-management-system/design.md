# 系统设计文档 - 个人健康管理系统

## 概述

个人健康管理系统是一个三层架构的Web应用，采用SpringBoot 3.X后端、Vue前端和MySQL数据库。系统支持三个主要角色（用户、医师、管理员），每个角色有不同的功能和权限。系统不使用复杂的安全认证框架，密码不加密，支持跨域访问。

## 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                     前端层 (Vue)                              │
│  ┌──────────────┬──────────────┬──────────────┐              │
│  │  用户端      │  医师端      │  管理端      │              │
│  │ (顶部菜单)   │ (侧边菜单)   │ (侧边菜单)   │              │
│  └──────────────┴──────────────┴──────────────┘              │
└─────────────────────────────────────────────────────────────┘
                          ↓ HTTP/REST API
┌─────────────────────────────────────────────────────────────┐
│                   后端层 (SpringBoot 3.X)                     │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  控制层 (Controller)                                  │   │
│  │  - 用户认证控制器                                    │   │
│  │  - 健康数据控制器                                    │   │
│  │  - 医师控制器                                        │   │
│  │  - 管理员控制器                                      │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  业务层 (Service)                                     │   │
│  │  - 用户服务                                          │   │
│  │  - 健康数据服务                                      │   │
│  │  - 医师服务                                          │   │
│  │  - 管理员服务                                        │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  数据访问层 (Repository)                              │   │
│  │  - 用户仓储                                          │   │
│  │  - 健康数据仓储                                      │   │
│  │  - 医师仓储                                          │   │
│  │  - 咨询记录仓储                                      │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                          ↓ JDBC
┌─────────────────────────────────────────────────────────────┐
│                   数据层 (MySQL)                              │
│  - 用户表                                                    │
│  - 医师表                                                    │
│  - 健康数据表                                                │
│  - 咨询记录表                                                │
│  - 健康建议表                                                │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈

- **后端**: SpringBoot 3.X, Spring Data JPA, MySQL Connector
- **前端**: Vue 3, Vue Router, Axios
- **数据库**: MySQL 8.0+
- **构建工具**: Maven (后端), npm/yarn (前端)
- **其他**: CORS支持, RESTful API

## 组件与接口设计

### 后端组件结构

```
src/main/java/com/health/
├── controller/
│   ├── AuthController.java          # 认证相关接口
│   ├── UserController.java          # 用户相关接口
│   ├── HealthDataController.java    # 健康数据接口
│   ├── DoctorController.java        # 医师相关接口
│   ├── ConsultationController.java  # 咨询相关接口
│   └── AdminController.java         # 管理员相关接口
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── HealthDataService.java
│   ├── DoctorService.java
│   ├── ConsultationService.java
│   └── AdminService.java
├── repository/
│   ├── UserRepository.java
│   ├── DoctorRepository.java
│   ├── HealthDataRepository.java
│   ├── ConsultationRepository.java
│   ├── HealthAdviceRepository.java
│   └── AuditLogRepository.java
├── entity/
│   ├── User.java
│   ├── Doctor.java
│   ├── HealthData.java
│   ├── Consultation.java
│   ├── HealthAdvice.java
│   └── AuditLog.java
├── dto/
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── HealthDataDTO.java
│   └── ConsultationDTO.java
├── config/
│   ├── CorsConfig.java              # CORS配置
│   └── JpaConfig.java
└── Application.java
```

### 前端组件结构

```
src/
├── components/
│   ├── LoginPage.vue                # 登录页面
│   ├── RegisterPage.vue             # 注册页面
│   ├── user/
│   │   ├── UserLayout.vue           # 用户端布局（顶部菜单）
│   │   ├── PersonalInfo.vue         # 个人信息管理
│   │   ├── HealthDataInput.vue      # 健康数据采集
│   │   ├── HealthTrend.vue          # 健康趋势分析
│   │   ├── HealthCheck.vue          # 常规健康检查
│   │   ├── GenderHealth.vue         # 性别健康监控
│   │   ├── Consultation.vue         # 在线咨询
│   │   └── HealthHistory.vue        # 健康历史查询
│   ├── doctor/
│   │   ├── DoctorLayout.vue         # 医师端布局（侧边菜单）
│   │   ├── PatientList.vue          # 患者列表
│   │   ├── PatientRecord.vue        # 患者档案查看
│   │   ├── DataReview.vue           # 数据审核与反馈
│   │   └── HealthAdvice.vue         # 健康建议推送
│   └── admin/
│       ├── AdminLayout.vue          # 管理端布局（侧边菜单）
│       ├── UserManagement.vue       # 用户权限管理
│       ├── DoctorManagement.vue     # 医师信息管理
│       ├── DataStatistics.vue       # 数据统计与分析
│       └── AuditLog.vue             # 审计日志
├── router/
│   └── index.js                     # 路由配置
├── services/
│   ├── api.js                       # API调用服务
│   └── auth.js                      # 认证服务
├── App.vue
└── main.js
```

## 数据模型设计

### 核心实体关系图

```
User (用户)
├── id (主键)
├── username (用户名)
├── password (密码)
├── email (邮箱)
├── name (姓名)
├── age (年龄)
├── gender (性别)
├── phone (电话)
├── role (角色: USER/DOCTOR/ADMIN)
├── status (状态: ACTIVE/INACTIVE)
├── created_at (创建时间)
└── updated_at (更新时间)

HealthData (健康数据)
├── id (主键)
├── user_id (外键 -> User)
├── height (身高)
├── weight (体重)
├── blood_pressure (血压)
├── heart_rate (心率)
├── diet_record (饮食记录)
├── exercise_record (运动记录)
├── data_type (数据类型: ROUTINE/GENDER_SPECIFIC)
├── recorded_at (记录时间)
└── created_at (创建时间)

Doctor (医师)
├── id (主键)
├── user_id (外键 -> User)
├── license_number (执业证号)
├── specialization (专科)
├── hospital (医院)
├── verified (是否认证)
└── created_at (创建时间)

Consultation (咨询)
├── id (主键)
├── user_id (外键 -> User)
├── doctor_id (外键 -> Doctor)
├── question (问题)
├── answer (回答)
├── status (状态: PENDING/ANSWERED)
├── created_at (创建时间)
└── answered_at (回答时间)

HealthAdvice (健康建议)
├── id (主键)
├── doctor_id (外键 -> Doctor)
├── user_id (外键 -> User)
├── advice_content (建议内容)
├── recommendation (推荐)
├── created_at (创建时间)
└── updated_at (更新时间)

AuditLog (审计日志)
├── id (主键)
├── user_id (外键 -> User)
├── action (操作)
├── resource (资源)
├── timestamp (时间戳)
└── details (详情)
```

### 数据库表设计

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
```

## 错误处理设计

### 错误响应格式

```json
{
  "code": 400,
  "message": "错误信息",
  "timestamp": "2024-01-01T12:00:00Z",
  "path": "/api/users/login"
}
```

### 常见错误码

- 200: 成功
- 400: 请求参数错误
- 401: 未授权（未登录）
- 403: 禁止访问（权限不足）
- 404: 资源不存在
- 500: 服务器内部错误

### 错误处理策略

- 所有API端点都应返回统一的错误响应格式
- 使用全局异常处理器捕获和处理异常
- 记录所有错误到审计日志
- 为用户提供清晰的中文错误提示

## 测试策略

### 单元测试

- 测试所有Service层的业务逻辑
- 测试数据验证和转换逻辑
- 测试Repository层的数据库操作

### 集成测试

- 测试Controller层的API端点
- 测试完整的业务流程（如用户注册→登录→提交健康数据）
- 测试数据库事务和一致性

### 功能测试

- 测试用户认证流程
- 测试健康数据的CRUD操作
- 测试医师查看患者档案的权限控制
- 测试管理员的权限管理功能

### 前端测试

- 测试Vue组件的渲染和交互
- 测试表单验证
- 测试API调用和错误处理

## 安全性设计

### 认证与授权

- 基于角色的访问控制（RBAC）
- 用户登录后使用Session或简单的Token机制
- 不同角色访问不同的功能模块

### 数据隐私

- 敏感数据（如性别健康数据）应有访问控制
- 医师只能查看其患者的数据
- 用户只能查看自己的数据

### CORS配置

- 配置允许的跨域请求源
- 支持所有HTTP方法（GET, POST, PUT, DELETE等）
- 允许自定义请求头

## 部署与配置

### 后端配置

- application.properties或application.yml配置数据库连接
- 配置CORS允许的源
- 配置日志级别为中文输出

### 前端配置

- 配置API基础URL
- 配置路由和导航
- 配置国际化为简体中文

### 数据库初始化

- 创建所有必要的表
- 插入初始管理员账户
- 创建必要的索引以提高查询性能
