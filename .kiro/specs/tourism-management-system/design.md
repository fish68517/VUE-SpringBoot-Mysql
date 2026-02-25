# 旅游管理系统 - 系统设计文档

## 概述

本系统是一个基于SpringBoot 3.x + Vue + MySQL的广州旅游管理平台。系统采用前后端分离架构，后端提供RESTful API接口，前端使用Vue框架构建用户界面。系统支持游客用户和管理员两类角色，提供景点票务、酒店预订、旅游商品购买、订单管理等核心功能，并突出展示广州特色旅游资源。

## 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                     前端层 (Vue)                              │
│  ┌──────────────┬──────────────┬──────────────┬────────────┐ │
│  │ 游客端       │ 管理员端     │ 公共组件     │ 工具库     │ │
│  └──────────────┴──────────────┴──────────────┴────────────┘ │
└─────────────────────────────────────────────────────────────┘
                          ↓ HTTP/REST
┌─────────────────────────────────────────────────────────────┐
│                   API网关层 (SpringBoot)                      │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │ 请求路由、参数验证、异常处理                              │ │
│  └──────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                   业务逻辑层 (Service)                        │
│  ┌──────────────┬──────────────┬──────────────┬────────────┐ │
│  │ 用户服务     │ 景点服务     │ 订单服务     │ 其他服务   │ │
│  └──────────────┴──────────────┴──────────────┴────────────┘ │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                   数据访问层 (Repository)                     │
│  ┌──────────────┬──────────────┬──────────────┬────────────┐ │
│  │ 用户仓储     │ 景点仓储     │ 订单仓储     │ 其他仓储   │ │
│  └──────────────┴──────────────┴──────────────┴────────────┘ │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                   数据库层 (MySQL)                            │
│  ┌──────────────┬──────────────┬──────────────┬────────────┐ │
│  │ 用户表       │ 景点表       │ 订单表       │ 其他表     │ │
│  └──────────────┴──────────────┴──────────────┴────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 分层设计

- **表现层**: Vue前端应用，包含游客端和管理员端
- **API层**: SpringBoot REST控制器，处理HTTP请求
- **业务逻辑层**: Service类，实现核心业务逻辑
- **数据访问层**: Repository接口和实现，与数据库交互
- **数据库层**: MySQL数据库，存储所有业务数据

## 组件与接口设计

### 后端核心模块

#### 1. 用户管理模块 (User Module)
- **UserController**: 处理用户注册、登录、个人信息管理
- **UserService**: 用户业务逻辑处理
- **UserRepository**: 用户数据访问
- **User实体**: 用户信息模型

#### 2. 景点管理模块 (Attraction Module)
- **AttractionController**: 处理景点查询、详情展示
- **AttractionService**: 景点业务逻辑处理
- **AttractionRepository**: 景点数据访问
- **Attraction实体**: 景点信息模型
- **AttractionTag实体**: 景点标签（广州特色标签）

#### 3. 酒店管理模块 (Hotel Module)
- **HotelController**: 处理酒店查询、详情展示
- **HotelService**: 酒店业务逻辑处理
- **HotelRepository**: 酒店数据访问
- **Hotel实体**: 酒店信息模型
- **HotelRoom实体**: 酒店房间类型模型

#### 4. 订单管理模块 (Order Module)
- **OrderController**: 处理订单创建、查询、更新
- **OrderService**: 订单业务逻辑处理
- **OrderRepository**: 订单数据访问
- **Order实体**: 订单信息模型
- **OrderItem实体**: 订单项目（景点门票、酒店、商品）

#### 5. 旅游商品模块 (Product Module)
- **ProductController**: 处理商品查询、详情展示
- **ProductService**: 商品业务逻辑处理
- **ProductRepository**: 商品数据访问
- **Product实体**: 商品信息模型

#### 6. 旅游路线模块 (Route Module)
- **RouteController**: 处理路线查询、推荐
- **RouteService**: 路线业务逻辑处理
- **RouteRepository**: 路线数据访问
- **Route实体**: 路线信息模型
- **RouteItem实体**: 路线中的景点/酒店项目

#### 7. 留言评价模块 (Comment Module)
- **CommentController**: 处理留言创建、查询
- **CommentService**: 留言业务逻辑处理
- **CommentRepository**: 留言数据访问
- **Comment实体**: 留言信息模型

#### 8. 公告管理模块 (Announcement Module)
- **AnnouncementController**: 处理公告创建、查询
- **AnnouncementService**: 公告业务逻辑处理
- **AnnouncementRepository**: 公告数据访问
- **Announcement实体**: 公告信息模型

#### 9. 收藏管理模块 (Favorite Module)
- **FavoriteController**: 处理收藏操作
- **FavoriteService**: 收藏业务逻辑处理
- **FavoriteRepository**: 收藏数据访问
- **Favorite实体**: 收藏信息模型

#### 10. 浏览历史模块 (BrowsingHistory Module)
- **BrowsingHistoryService**: 浏览历史业务逻辑处理
- **BrowsingHistoryRepository**: 浏览历史数据访问
- **BrowsingHistory实体**: 浏览历史模型

### 前端核心模块

#### 1. 游客端 (Tourist Client)
- **首页**: 展示广州特色景点、推荐路线、公告
- **景点模块**: 景点列表、详情、预订、评价
- **酒店模块**: 酒店列表、详情、预订
- **商品模块**: 商品列表、详情、购买
- **订单模块**: 订单列表、详情、取消
- **个人中心**: 个人资料、浏览历史、收藏、订单管理
- **路线推荐**: 根据时间和偏好推荐旅游路线

#### 2. 管理员端 (Admin Client)
- **用户管理**: 用户列表、审核、禁用/启用
- **景点管理**: 景点CRUD、标签管理
- **酒店管理**: 酒店CRUD、房间类型管理
- **商品管理**: 商品CRUD、库存管理
- **订单管理**: 订单列表、状态更新、统计
- **路线管理**: 路线CRUD、景点/酒店配置
- **留言管理**: 留言审核、置顶、删除
- **公告管理**: 公告CRUD
- **数据统计**: 订单统计、用户统计等

## 数据模型设计

### 核心数据表

#### 1. 用户表 (users)
```
- id (主键)
- username (用户名，唯一)
- password (密码)
- email (邮箱)
- phone (手机号)
- real_name (真实姓名)
- role (角色: 'tourist' 或 'admin')
- status (状态: 'active' 或 'disabled')
- created_at (创建时间)
- updated_at (更新时间)
```

#### 2. 景点表 (attractions)
```
- id (主键)
- name (景点名称)
- description (景点描述)
- location (位置)
- ticket_price (门票价格)
- opening_hours (营业时间)
- image_url (景点图片)
- is_guangzhou_special (是否为广州特色)
- created_at (创建时间)
- updated_at (更新时间)
```

#### 3. 景点标签表 (attraction_tags)
```
- id (主键)
- attraction_id (景点ID，外键)
- tag_name (标签名称: '美食', '文化', '历史' 等)
- created_at (创建时间)
```

#### 4. 酒店表 (hotels)
```
- id (主键)
- name (酒店名称)
- location (位置)
- description (酒店描述)
- image_url (酒店图片)
- rating (评分)
- created_at (创建时间)
- updated_at (更新时间)
```

#### 5. 酒店房间表 (hotel_rooms)
```
- id (主键)
- hotel_id (酒店ID，外键)
- room_type (房间类型: '单人间', '双人间' 等)
- price_per_night (每晚价格)
- quantity (房间数量)
- created_at (创建时间)
- updated_at (更新时间)
```

#### 6. 旅游商品表 (products)
```
- id (主键)
- name (商品名称)
- description (商品描述)
- price (价格)
- stock (库存)
- image_url (商品图片)
- category (分类: '美食', '手工艺品' 等)
- is_guangzhou_special (是否为广州特色)
- created_at (创建时间)
- updated_at (更新时间)
```

#### 7. 订单表 (orders)
```
- id (主键)
- order_number (订单号，唯一)
- user_id (用户ID，外键)
- total_price (总价格)
- status (状态: 'pending', 'confirmed', 'completed', 'cancelled')
- order_type (订单类型: 'attraction', 'hotel', 'product')
- created_at (创建时间)
- updated_at (更新时间)
```

#### 8. 订单项目表 (order_items)
```
- id (主键)
- order_id (订单ID，外键)
- item_type (项目类型: 'attraction', 'hotel', 'product')
- item_id (项目ID)
- quantity (数量)
- unit_price (单价)
- subtotal (小计)
- created_at (创建时间)
```

#### 9. 旅游路线表 (routes)
```
- id (主键)
- name (路线名称)
- description (路线描述)
- duration_days (天数)
- total_price (总价格)
- created_at (创建时间)
- updated_at (更新时间)
```

#### 10. 路线项目表 (route_items)
```
- id (主键)
- route_id (路线ID，外键)
- day_number (第几天)
- item_type (项目类型: 'attraction', 'hotel')
- item_id (项目ID)
- sequence (顺序)
- created_at (创建时间)
```

#### 11. 留言表 (comments)
```
- id (主键)
- user_id (用户ID，外键)
- target_type (目标类型: 'attraction', 'hotel')
- target_id (目标ID)
- content (留言内容)
- rating (评分: 1-5)
- status (状态: 'pending', 'approved', 'rejected')
- is_pinned (是否置顶)
- created_at (创建时间)
- updated_at (更新时间)
```

#### 12. 公告表 (announcements)
```
- id (主键)
- title (公告标题)
- content (公告内容)
- created_by (创建者ID，外键)
- created_at (创建时间)
- updated_at (更新时间)
```

#### 13. 收藏表 (favorites)
```
- id (主键)
- user_id (用户ID，外键)
- target_type (目标类型: 'attraction', 'hotel', 'product')
- target_id (目标ID)
- created_at (创建时间)
```

#### 14. 浏览历史表 (browsing_history)
```
- id (主键)
- user_id (用户ID，外键)
- target_type (目标类型: 'attraction', 'hotel', 'product')
- target_id (目标ID)
- created_at (创建时间)
```

## 错误处理设计

### 异常分类

1. **业务异常** (BusinessException)
   - 用户不存在
   - 订单不存在
   - 库存不足
   - 无效的操作

2. **验证异常** (ValidationException)
   - 参数验证失败
   - 数据格式错误

3. **权限异常** (AuthorizationException)
   - 无权限访问
   - 用户未登录

4. **系统异常** (SystemException)
   - 数据库错误
   - 服务器错误

### 错误响应格式

```json
{
  "code": "ERROR_CODE",
  "message": "错误信息描述",
  "timestamp": "2024-01-01T12:00:00Z",
  "data": null
}
```

### 全局异常处理

- 使用@ControllerAdvice和@ExceptionHandler进行全局异常处理
- 所有异常信息使用简体中文
- 返回统一的错误响应格式

## 测试策略

### 单元测试

- 测试Service层的业务逻辑
- 测试数据验证和转换
- 测试异常处理

### 集成测试

- 测试Controller和Service的集成
- 测试数据库操作
- 测试API端点

### 测试覆盖范围

- 用户认证和授权
- 订单创建和管理
- 景点和酒店查询
- 路线推荐逻辑
- 留言审核流程

## 安全设计

### 当前设计说明

根据需求，系统不采用认证框架和Session机制，密码不加密。为了快速完成项目，采用简化的安全设计：

1. **用户识别**: 通过用户ID在请求中传递（可通过URL参数或请求头）
2. **角色验证**: 在Controller层检查用户角色
3. **数据隔离**: 确保用户只能访问自己的数据

### 建议的改进方向

虽然当前不实现，但以下是生产环境应该考虑的安全措施：
- 实现JWT或Session认证
- 密码加密存储（bcrypt或类似）
- HTTPS通信
- SQL注入防护
- CSRF防护

## 性能考虑

### 数据库优化

- 为常用查询字段添加索引
- 使用分页查询大数据集
- 避免N+1查询问题

### 缓存策略

- 缓存景点、酒店等不经常变化的数据
- 缓存路线推荐结果

### API优化

- 使用分页和限制返回数据量
- 支持字段选择（只返回需要的字段）
- 使用异步处理长时间操作

## 部署架构

### 开发环境

- 本地MySQL数据库
- 本地SpringBoot应用
- 本地Vue开发服务器

### 生产环境建议

- 独立的MySQL数据库服务器
- SpringBoot应用服务器（可使用Docker容器化）
- 前端静态资源部署到CDN或Web服务器
- 使用反向代理（如Nginx）

## 技术栈总结

| 层级 | 技术 | 版本 |
|------|------|------|
| 前端 | Vue | 3.x |
| 后端 | SpringBoot | 3.x |
| 数据库 | MySQL | 5.7+ |
| 构建工具 | Maven | 3.6+ |
| 包管理 | npm | 8.x+ |
