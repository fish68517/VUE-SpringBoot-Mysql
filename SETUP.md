# 纳西族纹样数字化展示平台 - 项目设置指南

## 项目结构

```
.
├── VUE/                    # Vue.js 前端项目
│   ├── src/
│   │   ├── components/     # Vue 组件
│   │   ├── pages/          # 页面组件
│   │   ├── services/       # API 服务
│   │   ├── router/         # 路由配置
│   │   ├── store/          # Pinia 状态管理
│   │   ├── App.vue         # 根组件
│   │   └── main.js         # 入口文件
│   ├── package.json        # 前端依赖配置
│   ├── vite.config.js      # Vite 构建配置
│   └── .gitignore
├── SpringBoot/             # Spring Boot 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/naxi/
│   │   │   │   ├── entity/         # 数据实体
│   │   │   │   ├── repository/     # 数据仓储
│   │   │   │   ├── service/        # 业务服务
│   │   │   │   ├── controller/     # API 控制器
│   │   │   │   └── common/         # 通用工具
│   │   │   └── resources/
│   │   │       └── application.yml # 应用配置
│   │   └── test/
│   ├── pom.xml             # Maven 依赖配置
│   └── .gitignore
└── SETUP.md                # 本文件
```

## 前端设置

### 前置要求
- Node.js 14.0 或更高版本
- npm 6.0 或更高版本

### 安装步骤

1. 进入前端目录：
```bash
cd VUE
```

2. 安装依赖：
```bash
npm install
```

3. 创建 .env 文件（可选，使用默认值）：
```bash
cp .env.example .env
```

4. 启动开发服务器：
```bash
npm run dev
```

前端应用将在 `http://localhost:5173` 启动

### 构建生产版本
```bash
npm run build
```

## 后端设置

### 前置要求
- Java 11 或更高版本
- Maven 3.6 或更高版本
- MySQL 8.0 或更高版本

### 数据库设置

1. 创建数据库：
```sql
CREATE DATABASE naxi_pattern_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 更新 `SpringBoot/src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/naxi_pattern_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
    username: root
    password: your_password
```

### 安装步骤

1. 进入后端目录：
```bash
cd SpringBoot
```

2. 编译项目：
```bash
mvn clean compile
```

3. 运行应用：
```bash
mvn spring-boot:run
```

后端 API 将在 `http://localhost:8080` 启动

## API 文档

### 基础 URL
```
http://localhost:8080/api
```

### 用户管理接口
- `POST /users/register` - 用户注册
- `POST /users/login` - 用户登录
- `GET /users/{id}` - 获取用户信息
- `PUT /users/{id}` - 更新用户信息
- `PUT /users/{id}/password` - 修改密码

### 纹样管理接口
- `GET /patterns` - 获取纹样列表
- `GET /patterns/{id}` - 获取纹样详情
- `POST /patterns` - 创建纹样（管理员）
- `PUT /patterns/{id}` - 更新纹样（管理员）
- `DELETE /patterns/{id}` - 删除纹样（管理员）
- `GET /patterns/search` - 搜索纹样

## 开发工作流

1. 启动后端服务：
```bash
cd SpringBoot
mvn spring-boot:run
```

2. 在新终端启动前端开发服务器：
```bash
cd VUE
npm run dev
```

3. 在浏览器中打开 `http://localhost:5173`

## 常见问题

### 前端无法连接到后端
- 确保后端服务运行在 `http://localhost:8080`
- 检查 `VUE/vite.config.js` 中的代理配置
- 检查浏览器控制台的 CORS 错误

### 数据库连接失败
- 确保 MySQL 服务正在运行
- 验证数据库连接信息是否正确
- 检查数据库是否已创建

### 端口被占用
- 修改 `VUE/vite.config.js` 中的 `port` 配置
- 修改 `SpringBoot/src/main/resources/application.yml` 中的 `server.port` 配置

## 下一步

完成项目初始化后，请参考 `.kiro/specs/naxi-pattern-platform/tasks.md` 中的实现计划继续开发。
