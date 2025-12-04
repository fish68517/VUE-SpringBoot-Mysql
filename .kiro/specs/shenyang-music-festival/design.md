# 沈阳音乐节管理系统 - 设计文档

## 概述

沈阳音乐节管理系统采用前后端分离架构，后端基于 Spring Boot 3.x + Spring Data JPA + MySQL 8，前端基于 Vue3 + Pinia + Element Plus。系统分为用户端（C 端）和管理端（B 端）两个应用，通过 RESTful API 进行通信。

**核心设计原则：**
- 分层架构：Controller → Service → Repository → Entity
- 防黄牛机制：同一身份证号限购一张票
- 积分驱动：通过打卡任务和消费行为激励用户参与
- 地域特色：融合沈阳本地文化元素（满族文化、本土乐队、地方美食）

---

## 架构设计

### 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                     前端应用层                               │
├──────────────────────┬──────────────────────────────────────┤
│   用户端 (C 端)      │        管理端 (B 端)                 │
│  ┌────────────────┐  │  ┌────────────────────────────────┐ │
│  │   顶部导航栏   │  │  │      顶部导航栏 + 用户信息     │ │
│  ├────────────────┤  │  ├────────────────────────────────┤ │
│  │ 左侧菜单栏 │内容 │  │ 左侧菜单栏 │      内容区域      │ │
│  │ - 首页     │区域 │  │ - 仪表盘   │                    │ │
│  │ - 购票     │    │  │ - 用户管理 │                    │ │
│  │ - 商城     │    │  │ - 票务管理 │                    │ │
│  │ - 打卡     │    │  │ - 商品管理 │                    │ │
│  │ - 个人中心 │    │  │ - 内容管理 │                    │ │
│  │ - 积分商城 │    │  │ - 打卡审核 │                    │ │
│  │ - 我的订单 │    │  │ - 积分管理 │                    │ │
│  │ - 我的电子票│   │  │ - 数据导出 │                    │ │
│  └────────────────┘  │  └────────────────────────────────┘ │
└──────────────────────┴──────────────────────────────────────┘
                            ↓ Axios + 拦截器
┌─────────────────────────────────────────────────────────────┐
│                    API 网关层                                │
│  - 请求路由                                                 │
│  - 认证授权                                                 │
│  - 速率限制                                                 │
└─────────────────────────────────────────────────────────────┘
                            ↓ RESTful API
┌─────────────────────────────────────────────────────────────┐
│                  后端应用层 (Spring Boot)                    │
├──────────────────────────────────────────────────────────────┤
│  Controller 层                                               │
│  - UserController, TicketController, ProductController      │
│  - CheckinController, PointsController, AdminController     │
├──────────────────────────────────────────────────────────────┤
│  Service 层                                                  │
│  - UserService, TicketService, ProductService               │
│  - CheckinService, PointsService, AdminService              │
├──────────────────────────────────────────────────────────────┤
│  Repository 层 (Spring Data JPA)                             │
│  - UserRepository, TicketRepository, ProductRepository      │
│  - CheckinRepository, PointsRepository                       │
├──────────────────────────────────────────────────────────────┤
│  Entity 层                                                   │
│  - User, Ticket, Product, Checkin, Points, Order            │
└──────────────────────────────────────────────────────────────┘
                            ↓ JDBC
┌─────────────────────────────────────────────────────────────┐
│                    MySQL 8 数据库                            │
│  - 用户表, 票务表, 商品表, 订单表, 积分表                   │
└─────────────────────────────────────────────────────────────┘
```

### 前端 UI 布局设计

#### 用户端 (C 端) 布局

```
┌─────────────────────────────────────────────────────────────┐
│  Logo           
├─────────────────────────────────────────────────────────────┤
│ 首页   │                                                     │
│ 购票   │                                                     │
│ 商城   │              内容区域                              │
│ 打卡   │         (根据菜单动态切换)                         │
│ 个人中心│                                                     │
│ 积分商城│                                                     │
│ 我的订单│                                                     │
│ 我的电子票│                                                   │
│        │                                                     │
└─────────────────────────────────────────────────────────────┘
```

**菜单项对应内容：**
- **首页**：轮播图、倒计时、艺人列表、天气、交通信息
- **购票**：场次选择 → 分区选择 → 实名信息填写 → 支付 → 电子票生成
- **商城**：商品分类 → 商品列表 → 商品详情 → 购物车 → 结算
- **打卡**：打卡任务列表 → 任务详情 → 位置验证 → 照片上传 → 审核状态
- **个人中心**：用户信息编辑、实名认证、收货地址管理、登录日志
- **积分商城**：积分余额显示 → 商品列表 → 兑换申请 → 兑换记录
- **我的订单**：票务订单列表、商品订单列表、订单详情、物流追踪
- **我的电子票**：电子票列表、二维码显示、PDF 下载

#### 管理端 (B 端) 布局

```
┌─────────────────────────────────────────────────────────────┐
│  Logo        │
├─────────────────────────────────────────────────────────────┤
│ 仪表盘 │                                                     │
│ 用户管理│                                                     │
│ 票务管理│              内容区域                              │
│ 商品管理│         (根据菜单动态切换)                         │
│ 订单管理│                                                     │
│ 内容管理│                                                     │
│ 打卡审核│                                                     │
│ 积分管理│                                                     │
│ 数据导出│                                                     │
│        │                                                     │
└─────────────────────────────────────────────────────────────┘
```

**菜单项对应内容：**
- **仪表盘**：订单趋势图、用户增长曲线、分区售票热力图、商品销量排行、打卡统计
- **用户管理**：用户列表、搜索筛选、用户详情、封禁/解封、积分调整、导出 Excel
- **票务管理**：场次管理、分区管理、库存设置、价格设置、订单列表、订单详情
- **商品管理**：分类管理、商品列表、上下架、库存管理、库存预警、编辑商品
- **订单管理**：票务订单列表、商品订单列表、订单详情、物流单号、导出 Excel
- **内容管理**：轮播图管理、文章发布、艺人信息维护、演出日程编辑、静态页编辑
- **打卡审核**：打卡记录列表、照片审核、批量审核、拒绝原因、打卡统计
- **积分管理**：积分商品管理、兑换订单审核、积分流水查询、用户积分调整、冻结积分
- **数据导出**：订单导出、用户导出、积分流水导出、打卡统计导出

### 技术栈详情

**后端：**
- Spring Boot 3.x：核心框架
- Spring Web MVC：HTTP 请求处理
- Spring Data JPA：数据持久化
- MySQL 8：关系型数据库
- Lombok：代码简化
- Maven：项目构建

**前端：**
- Vue3 + Composition API：UI 框架
- Pinia：状态管理（用户信息、购物车、打卡状态）
- Vue Router：路由管理
- Element Plus：UI 组件库
- Arco Design：部分高级组件
- Vite 5：构建工具
- Axios：HTTP 客户端
- ECharts 5：数据可视化
- html2canvas / pdf-make：PDF 生成
- **语言支持**：仅支持简体中文，所有页面、菜单、提示信息均为中文

---

## 组件与接口设计

### 后端 API 接口清单

#### 用户认证模块
```
POST   /api/auth/register          - 用户注册
POST   /api/auth/login             - 用户登录
POST   /api/auth/send-sms          - 发送短信验证码
POST   /api/auth/verify-sms        - 验证短信验证码
POST   /api/auth/logout            - 用户登出
```

#### 用户信息模块
```
GET    /api/users/profile          - 获取用户信息
PUT    /api/users/profile          - 更新用户信息
POST   /api/users/real-name        - 实名认证
GET    /api/users/real-name        - 获取实名认证状态
PUT    /api/users/avatar           - 上传头像
```

#### 音乐节信息模块
```
GET    /api/festival/info          - 获取音乐节基本信息
GET    /api/festival/schedule      - 获取演出日程
GET    /api/artists                - 获取艺人列表
GET    /api/artists/:id            - 获取艺人详情
GET    /api/weather                - 获取天气信息
GET    /api/transport              - 获取交通信息
```

#### 购票模块
```
GET    /api/tickets/sessions       - 获取可用场次
GET    /api/tickets/zones          - 获取分区信息
GET    /api/tickets/zones/:id      - 获取分区详情（库存、价格）
POST   /api/tickets/check-id       - 检查身份证是否已购票
POST   /api/tickets/order          - 创建购票订单
GET    /api/tickets/my-tickets     - 获取我的电子票
GET    /api/tickets/:id/pdf        - 下载电子票 PDF
```

#### 商品模块
```
GET    /api/products               - 获取商品列表
GET    /api/products/:id           - 获取商品详情
GET    /api/products/categories    - 获取商品分类
POST   /api/cart/add               - 加入购物车
GET    /api/cart                   - 获取购物车
DELETE /api/cart/:id               - 删除购物车项
POST   /api/orders/product         - 创建商品订单
GET    /api/orders/product         - 获取我的商品订单
```

#### 打卡任务模块
```
GET    /api/checkins/tasks         - 获取打卡任务列表
GET    /api/checkins/tasks/:id     - 获取任务详情
POST   /api/checkins/verify-location - 验证用户位置
POST   /api/checkins/submit        - 提交打卡记录
GET    /api/checkins/my-records    - 获取我的打卡记录
```

#### 积分模块
```
GET    /api/points/balance         - 获取用户积分余额
GET    /api/points/history         - 获取积分流水
GET    /api/points/mall            - 获取积分商城商品
POST   /api/points/exchange        - 提交兑换申请
GET    /api/points/exchange-orders - 获取我的兑换订单
```

#### 内容互动模块
```
GET    /api/articles               - 获取文章列表
GET    /api/articles/:id           - 获取文章详情
POST   /api/articles/:id/like      - 点赞文章
POST   /api/articles/:id/collect   - 收藏文章
POST   /api/articles/:id/comment   - 评论文章
GET    /api/articles/:id/comments  - 获取文章评论
POST   /api/festival/follow        - 关注音乐节
GET    /api/festival/followers     - 获取关注状态
```

#### 管理员模块
```
GET    /api/admin/dashboard        - 获取仪表盘数据
GET    /api/admin/users            - 获取用户列表
PUT    /api/admin/users/:id/ban    - 封禁用户
PUT    /api/admin/users/:id/points - 调整用户积分
GET    /api/admin/tickets/orders   - 获取票务订单
GET    /api/admin/products/orders  - 获取商品订单
POST   /api/admin/products         - 创建商品
PUT    /api/admin/products/:id     - 编辑商品
DELETE /api/admin/products/:id     - 删除商品
GET    /api/admin/checkins/records - 获取打卡记录
PUT    /api/admin/checkins/:id     - 审核打卡记录
GET    /api/admin/points/mall      - 获取积分商城商品
POST   /api/admin/points/mall      - 创建积分商品
PUT    /api/admin/points/exchange/:id - 审核兑换申请
GET    /api/admin/articles         - 获取文章列表
POST   /api/admin/articles         - 发布文章
PUT    /api/admin/articles/:id     - 编辑文章
DELETE /api/admin/articles/:id     - 删除文章
```

---

## 数据模型设计

### 核心实体关系图

```
User (用户)
├── id (PK)
├── phone (唯一)
├── password
├── nickname
├── avatar
├── email
├── realName
├── idNumber (唯一，用于防黄牛)
├── isRealNameVerified
├── points (积分余额)
├── isBlocked (是否被封禁)
├── createdAt
└── updatedAt

Ticket (电子票)
├── id (PK)
├── userId (FK → User)
├── sessionId (FK → TicketSession)
├── zoneId (FK → TicketZone)
├── buyerIdNumber (购票人身份证)
├── buyerName (购票人姓名)
├── qrCode (二维码)
├── status (已支付/已使用)
├── createdAt
└── updatedAt

TicketSession (场次)
├── id (PK)
├── name
├── startTime
├── endTime
├── status (进行中/已结束)
└── createdAt

TicketZone (分区)
├── id (PK)
├── sessionId (FK → TicketSession)
├── name (A区/B区/站区/VIP区)
├── totalCapacity (总容纳人数)
├── soldCount (已售数量)
├── price
├── createdAt
└── updatedAt

Product (商品)
├── id (PK)
├── categoryId (FK → ProductCategory)
├── name
├── description
├── images (多图)
├── originalPrice
├── currentPrice
├── stock
├── specs (规格：颜色、尺码等)
├── isActive (是否上架)
├── createdAt
└── updatedAt

ProductCategory (商品分类)
├── id (PK)
├── name
├── description
└── createdAt

Order (订单)
├── id (PK)
├── userId (FK → User)
├── orderType (ticket/product)
├── totalAmount
├── status (已支付/已发货/已完成)
├── shippingAddress
├── trackingNumber
├── createdAt
└── updatedAt

OrderItem (订单项)
├── id (PK)
├── orderId (FK → Order)
├── productId (FK → Product)
├── quantity
├── unitPrice
└── createdAt

CheckinTask (打卡任务)
├── id (PK)
├── name
├── description
├── coverImage
├── points (奖励积分)
├── latitude (地理位置纬度)
├── longitude (地理位置经度)
├── radius (地理围栏半径，单位：米)
├── startTime
├── endTime
├── status (进行中/已结束)
├── createdAt
└── updatedAt

CheckinRecord (打卡记录)
├── id (PK)
├── userId (FK → User)
├── taskId (FK → CheckinTask)
├── photo (照片)
├── latitude (打卡位置纬度)
├── longitude (打卡位置经度)
├── status (待审核/已通过/已拒绝)
├── rejectReason (拒绝原因)
├── createdAt
└── updatedAt

PointsMall (积分商城商品)
├── id (PK)
├── name
├── description
├── images
├── pointsRequired (所需积分)
├── type (实体/虚拟)
├── stock
├── countdownEndTime (倒计时结束时间)
├── isActive (是否上架)
├── createdAt
└── updatedAt

PointsExchange (积分兑换订单)
├── id (PK)
├── userId (FK → User)
├── mallItemId (FK → PointsMall)
├── pointsUsed (使用积分)
├── status (待审核/已通过/已发货/已完成)
├── shippingAddress
├── trackingNumber
├── createdAt
└── updatedAt

PointsHistory (积分流水)
├── id (PK)
├── userId (FK → User)
├── changeAmount (变动积分)
├── changeType (打卡/购票/兑换/手动调整)
├── relatedId (关联的任务/订单 ID)
├── description
├── createdAt
└── updatedAt

Article (文章/资讯)
├── id (PK)
├── title
├── content (富文本)
├── images (多图)
├── author
├── type (新闻/攻略/美食推荐等)
├── likes
├── views
├── isPublished
├── createdAt
└── updatedAt

ArticleComment (文章评论)
├── id (PK)
├── articleId (FK → Article)
├── userId (FK → User)
├── content
├── likes
├── createdAt
└── updatedAt

UserCollection (用户收藏)
├── id (PK)
├── userId (FK → User)
├── articleId (FK → Article)
├── createdAt
└── updatedAt

FestivalFollow (音乐节关注)
├── id (PK)
├── userId (FK → User)
├── followedAt
└── updatedAt
```

---

## 错误处理设计

### 统一错误响应格式

```json
{
  "code": 400,
  "message": "错误描述",
  "data": null,
  "timestamp": "2024-12-04T10:30:00Z"
}
```

### 常见错误码

| 错误码 | 描述 | 处理方式 |
|--------|------|--------|
| 200 | 成功 | 返回数据 |
| 400 | 请求参数错误 | 前端验证 + 后端验证 |
| 401 | 未授权（未登录） | 跳转登录页 |
| 403 | 禁止访问（无权限） | 显示无权限提示 |
| 404 | 资源不存在 | 显示 404 页面 |
| 409 | 冲突（如身份证已购票） | 显示具体冲突信息 |
| 500 | 服务器错误 | 显示通用错误提示 |

### 业务异常处理

- **防黄牛异常**：同一身份证号已购票 → 返回 409 + 提示信息
- **库存不足异常**：分区票量已售罄 → 返回 409 + 提示"已售罄"
- **积分不足异常**：用户积分不足 → 返回 400 + 提示"积分不足"
- **位置验证失败**：用户不在地理围栏范围内 → 返回 400 + 提示"位置不符"
- **用户被封禁**：用户被管理员封禁 → 返回 403 + 提示"账户已被封禁"

---

## 测试策略

### 单元测试

**测试范围：**
- Service 层业务逻辑（防黄牛、积分计算、库存扣减等）
- Repository 层数据访问（CRUD 操作）
- 工具类（日期处理、地理位置计算等）

**测试框架：** JUnit 5 + Mockito

**关键测试用例：**
1. 防黄牛机制：同一身份证号购票两次应被拒绝
2. 库存扣减：购票成功后分区库存应减少
3. 积分计算：打卡通过后用户积分应增加
4. 地理围栏验证：位置在范围内应通过，范围外应拒绝

### 集成测试

**测试范围：**
- 完整的购票流程（选分区 → 填信息 → 支付 → 生成电子票）
- 完整的打卡流程（位置验证 → 上传照片 → 审核 → 积分增加）
- 完整的兑换流程（选商品 → 提交申请 → 审核 → 扣积分）

**测试工具：** Spring Boot Test + MockMvc

### 前端测试

**测试范围：**
- 组件单元测试（表单验证、状态管理等）
- 路由导航测试
- API 调用和错误处理

**测试框架：** Vitest + Vue Test Utils

---

## 国际化与本地化设计

### 语言支持

**前端应用（Vue 页面）：**
- 仅支持**简体中文**
- 所有用户界面、菜单、按钮、提示信息、错误提示均为中文
- 不需要实现多语言切换功能
- 不需要集成 i18n 国际化库

**后端应用（API 响应）：**
- API 返回的错误信息、提示信息均为中文
- 数据库中的枚举值、状态描述均为中文
- 不需要支持多语言 API 响应

### 本地化内容

- **时间格式**：使用中国标准时间格式（YYYY-MM-DD HH:mm:ss）
- **货币单位**：使用人民币（¥）
- **地理位置**：沈阳本地地名、地标、交通信息
- **文化元素**：融合沈阳本地文化、满族文化、地方美食等

---

## 安全设计

### 认证与授权

- **认证方式**：基于 JWT Token（不使用 Spring Security）
- **Token 生成**：登录成功后生成 JWT Token，包含用户 ID 和角色信息
- **Token 验证**：每个请求都需要在 Header 中携带 Token，后端验证 Token 有效性
- **Token 过期**：设置 Token 过期时间为 24 小时，过期后需要重新登录
- **刷新机制**：提供 Refresh Token 用于获取新的 Access Token

### 数据安全

- **身份证号脱敏**：在前端和管理后台显示身份证号时，仅显示后四位
- **密码加密**：使用 BCrypt 算法加密存储用户密码
- **敏感数据加密**：收货地址等敏感信息在数据库中加密存储
- **SQL 注入防护**：使用 JPA 参数化查询，防止 SQL 注入

### 业务安全

- **防黄牛**：同一身份证号限购一张票，通过数据库唯一约束实现
- **防刷分**：管理员可冻结用户积分，防止恶意刷分
- **防重复打卡**：同一用户同一任务只能打卡一次
- **地理围栏验证**：打卡时验证用户位置是否在指定范围内
- **图片审核**：管理员手动审核打卡照片，防止虚假打卡

---

## 性能优化

### 数据库优化

- **索引设计**：在 userId、idNumber、sessionId、zoneId 等常用查询字段上建立索引
- **查询优化**：使用 JPA 的 @Query 注解编写高效的 SQL 查询
- **分页查询**：订单列表、文章列表等大数据量查询使用分页
- **缓存策略**：使用 Redis 缓存热点数据（艺人列表、商品列表等）

### 前端优化

- **代码分割**：使用 Vue Router 的动态导入实现路由级别的代码分割
- **图片优化**：使用 WebP 格式、懒加载等技术优化图片加载
- **状态管理**：使用 Pinia 管理全局状态，避免不必要的重新渲染
- **虚拟滚动**：长列表使用虚拟滚动提高渲染性能

### API 优化

- **速率限制**：限制单个用户的请求频率，防止滥用
- **请求合并**：减少不必要的 API 调用，合并多个请求
- **响应压缩**：启用 Gzip 压缩减少传输数据量

---

## 部署架构

### 开发环境

```
本地开发
├── 后端：Spring Boot 本地运行 (localhost:8080)
├── 前端：Vite 开发服务器 (localhost:5173)
└── 数据库：本地 MySQL 8
```

### 生产环境

```
生产部署
├── 后端：Docker 容器 + Nginx 反向代理
├── 前端：静态文件 + CDN
├── 数据库：云数据库 MySQL 8
├── 文件存储：对象存储服务（如 OSS）
└── 监控告警：日志系统 + 性能监控
```

---

## 关键技术亮点

### 1. 防黄牛机制

通过在 idNumber 字段上建立唯一约束，确保同一身份证号只能购买一张票。在购票时检查身份证号是否已存在，防止重复购票。

### 2. 地理围栏打卡

使用经纬度和半径定义地理范围，在打卡时计算用户位置与任务位置的距离，判断是否在范围内。公式：
```
distance = sqrt((lat1-lat2)² + (lon1-lon2)²) * 111000 (米)
```

### 3. 积分规则引擎

通过 PointsHistory 表记录所有积分变动，支持按用户、时间、任务类型筛选。管理员可手动调整积分，系统自动记录操作原因。

### 4. 电子票 PDF 生成

使用 html2canvas 或 pdf-make 库将电子票 HTML 转换为 PDF，包含二维码和身份证后四位，用户可下载保存。

### 5. 沈阳地域特色

- 突出沈阳本土乐队标注
- 打卡任务融合满族文化、地方美食等元素
- 积分商城商品为沈阳音乐节限定周边
- 天气、交通、美食推荐等本地化内容

