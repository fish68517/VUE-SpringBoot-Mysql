# 星落宠物用品销售网站

一个功能完整的B2C宠物电商平台，集成了在线商城、宠物社区和多角色管理功能。

## 项目概述

星落宠物用品销售网站系统支持三种用户角色：
- **普通用户**：浏览商品、购物、评价、参与社区
- **店家**：管理店铺、商品、订单、优惠券
- **管理员**：管理用户、店家、商品审核、社区内容审核、数据统计

### 核心功能

#### 用户端功能
- 用户注册与登录
- 个人资料管理
- 宠物档案管理
- 商品浏览与搜索
- 个性化推荐
- 购物车管理
- 订单创建与管理
- 商品评价
- 宠物社区互动

#### 店家端功能
- 店铺信息管理
- 商品管理（增删改查、上下架）
- 库存管理
- 订单处理与发货
- 优惠券管理

#### 管理员功能
- 用户管理
- 店家管理与审核
- 商品审核
- 社区内容审核
- 数据统计看板

## 技术栈

### 后端
- **框架**：Spring Boot 3.2.6
- **ORM**：Spring Data JPA
- **数据库**：MySQL 8.0
- **构建工具**：Maven
- **其他**：Lombok

### 前端
- **框架**：Vue 3 (Composition API)
- **UI库**：Element Plus
- **状态管理**：Pinia
- **HTTP客户端**：Axios
- **路由**：Vue Router
- **构建工具**：Vite

## 项目结构

```
星落宠物用品销售网站/
├── SpringBoot/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/xingluo/petshop/
│   │   │   │   ├── common/           # 公共模块
│   │   │   │   ├── config/           # 配置模块
│   │   │   │   ├── entity/           # 实体类
│   │   │   │   ├── repository/       # JPA Repository
│   │   │   │   ├── service/          # 业务逻辑
│   │   │   │   ├── controller/       # 控制器
│   │   │   │   └── dto/              # 数据传输对象
│   │   │   └── resources/
│   │   │       ├── application.yml   # 应用配置
│   │   │       └── schema.sql        # 数据库初始化脚本
│   │   └── test/                     # 测试代码
│   ├── pom.xml                       # Maven配置
│   └── README.md                     # 后端说明文档
│
├── VUE/                        # 前端项目
│   ├── src/
│   │   ├── api/                # API接口封装
│   │   ├── store/              # Pinia状态管理
│   │   ├── router/             # 路由配置
│   │   ├── views/              # 页面组件
│   │   ├── components/         # 公共组件
│   │   ├── utils/              # 工具函数
│   │   ├── App.vue             # 根组件
│   │   └── main.js             # 入口文件
│   ├── package.json            # npm配置
│   ├── vite.config.js          # Vite配置
│   └── README.md               # 前端说明文档
│
├── doc/                        # 项目文档
│   ├── 需求文档.md
│   ├── 设计文档.md
│   └── 部署指南.md
│
└── README.md                   # 本文件
```

## 快速开始

### 前置要求

- Node.js 16.0+
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### 后端启动

1. **配置数据库**

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE petshop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;

# 导入初始化脚本
mysql -u root -p petshop < SpringBoot/src/main/resources/schema.sql
```

2. **配置应用**

编辑 `SpringBoot/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/petshop?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

3. **启动应用**

```bash
cd SpringBoot
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. **安装依赖**

```bash
cd VUE
npm install
```

2. **启动开发服务器**

```bash
npm run dev
```

前端应用将在 `http://localhost:5173` 启动

3. **构建生产版本**

```bash
npm run build
```

## API 文档

### 基础信息

- **基础URL**：`http://localhost:8080/api`
- **请求格式**：JSON
- **响应格式**：JSON

### 统一响应格式

所有API返回统一的JSON格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（未登录） |
| 403 | 禁止访问（无权限） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 主要API端点

#### 用户模块
- `POST /user/register` - 用户注册
- `POST /user/login` - 用户登录
- `GET /user/profile` - 获取用户信息
- `PUT /user/profile` - 更新用户信息

#### 商品模块
- `GET /product/list` - 获取商品列表（分页）
- `GET /product/{id}` - 获取商品详情
- `GET /product/search` - 搜索商品
- `GET /product/category/{id}` - 按分类获取商品
- `GET /product/recommend` - 获取推荐商品

#### 购物车模块
- `POST /cart` - 添加到购物车
- `GET /cart/list` - 获取购物车列表
- `PUT /cart/{id}` - 更新购物车商品数量
- `DELETE /cart/{id}` - 删除购物车商品

#### 订单模块
- `POST /order` - 创建订单
- `GET /order/list` - 获取订单列表
- `GET /order/{id}` - 获取订单详情
- `PUT /order/{id}/pay` - 支付订单
- `PUT /order/{id}/cancel` - 取消订单

#### 社区模块
- `POST /community/post` - 发布帖子
- `GET /community/post/list` - 获取帖子列表
- `GET /community/post/{id}` - 获取帖子详情
- `POST /community/reply` - 发布评论
- `POST /community/like/{postId}` - 点赞/取消点赞

更多API详情请参考 `doc/API文档.md`

## 数据库设计

系统包含15个主要数据表：

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| pet | 宠物档案表 |
| shop | 店铺表 |
| category | 商品分类表 |
| product | 商品表 |
| cart | 购物车表 |
| orders | 订单表 |
| order_item | 订单明细表 |
| review | 商品评价表 |
| community_post | 社区帖子表 |
| community_reply | 社区评论表 |
| post_like | 帖子点赞表 |
| coupon | 优惠券表 |
| user_coupon | 用户优惠券表 |
| browse_history | 浏览历史表 |

详细的数据库设计文档请参考 `doc/数据库设计.md`

## 开发指南

### 代码规范

#### 后端
- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- 所有类和方法添加中文注释
- 使用统一的异常处理机制

#### 前端
- 遵循 Vue 3 官方风格指南
- 使用 ESLint 检查代码质量
- 组件命名使用 PascalCase
- 文件命名使用 kebab-case

### Git 提交规范

```
<type>(<scope>): <subject>

<body>

<footer>
```

类型说明：
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建工具或依赖更新

### 本地开发流程

1. 创建功能分支：`git checkout -b feature/xxx`
2. 开发并提交代码：`git commit -m "feat: xxx"`
3. 推送到远程：`git push origin feature/xxx`
4. 创建 Pull Request 进行代码审查
5. 合并到主分支

## 测试

### 后端测试

运行所有测试：
```bash
cd SpringBoot
mvn test
```

运行特定测试类：
```bash
mvn test -Dtest=UserServiceTest
```

### 前端测试

运行所有测试：
```bash
cd VUE
npm run test
```

运行特定测试文件：
```bash
npm run test -- ProductCard.spec.js
```

## 部署

### 开发环境部署

详见 `doc/部署指南.md` 中的开发环境部分

### 生产环境部署

详见 `doc/部署指南.md` 中的生产环境部分

### Docker 部署

详见 `doc/Docker部署指南.md`

## 常见问题

### Q: 如何修改数据库连接信息？
A: 编辑 `SpringBoot/src/main/resources/application.yml` 中的数据库配置

### Q: 前端如何修改后端API地址？
A: 编辑 `VUE/src/utils/request.js` 中的 `baseURL` 配置

### Q: 如何添加新的商品分类？
A: 通过管理员后台或直接在数据库中插入数据到 `category` 表

### Q: 如何重置数据库？
A: 删除数据库后重新创建并导入 `schema.sql`

## 性能优化建议

1. **数据库优化**
   - 为常用查询字段添加索引
   - 使用分页查询避免一次性加载大量数据
   - 定期分析和优化慢查询

2. **缓存策略**
   - 缓存商品分类数据（变化频率低）
   - 缓存热门商品列表
   - 使用 Redis 进行分布式缓存

3. **前端优化**
   - 图片懒加载
   - 路由懒加载
   - 组件按需引入
   - 使用虚拟滚动处理长列表

4. **后端优化**
   - 使用 DTO 减少数据传输量
   - 避免返回不必要的关联数据
   - 使用异步处理耗时操作

## 安全建议

1. **认证与授权**
   - 实现 JWT Token 认证（当前使用简单会话）
   - 添加权限控制中间件
   - 实现接口级别的权限验证

2. **数据安全**
   - 使用加密存储密码（当前为毕设要求使用明文）
   - 对敏感信息进行加密
   - 定期备份数据库

3. **输入验证**
   - 验证所有用户输入
   - 防止 SQL 注入
   - 防止 XSS 攻击

4. **HTTPS**
   - 生产环境使用 HTTPS
   - 配置 SSL 证书

## 故障排查

### 后端无法启动
- 检查 MySQL 是否运行
- 检查数据库连接配置
- 查看日志文件获取详细错误信息

### 前端无法连接后端
- 检查后端是否正常运行
- 检查 CORS 配置
- 检查网络连接

### 数据库连接失败
- 检查 MySQL 用户名和密码
- 检查数据库是否存在
- 检查防火墙设置

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

本项目仅供学习和研究使用。

## 联系方式

如有问题或建议，请联系项目维护者。

## 更新日志

### v1.0.0 (2024-11-19)
- 初始版本发布
- 完成所有核心功能
- 支持三种用户角色
- 包含完整的API接口
