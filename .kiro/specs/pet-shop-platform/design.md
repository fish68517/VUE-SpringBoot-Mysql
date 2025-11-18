# 设计文档

## 概述

星落宠物用品销售网站系统采用前后端分离架构，后端使用 Spring Boot 3.x + JPA 提供 RESTful API，前端使用 Vue 3 + Element Plus 构建用户界面。系统分为三个主要模块：用户端、店家端和管理员端，通过统一的后端服务提供支持。

### 技术栈

**后端：**
- Spring Boot 3.2.6
- Spring Data JPA
- MySQL 8.0
- Lombok
- Maven

**前端：**
- Vue 3 (Composition API)
- Element Plus
- Pinia (状态管理)
- Axios (HTTP 客户端)
- Vue Router

## 架构设计

### 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                    前端层 (Vue 3)                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │ 用户端   │  │ 店家端   │  │ 管理端   │              │
│  └──────────┘  └──────────┘  └──────────┘              │
└─────────────────────────────────────────────────────────┘
                        │ HTTP/JSON
                        ▼
┌─────────────────────────────────────────────────────────┐
│              后端层 (Spring Boot)                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │           Controller 层 (REST API)                │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │           Service 层 (业务逻辑)                   │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │           Repository 层 (JPA)                     │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                        │ JDBC
                        ▼
┌─────────────────────────────────────────────────────────┐
│                  数据层 (MySQL 8.0)                      │
└─────────────────────────────────────────────────────────┘
```

### 后端模块结构

```
com.xingluo.petshop
├── common/              # 公共模块
│   ├── Result.java      # 统一响应封装
│   ├── ResultCode.java  # 响应状态码
│   └── exception/       # 异常处理
│       ├── GlobalExceptionHandler.java
│       └── BusinessException.java
├── config/              # 配置模块
│   ├── CorsConfig.java  # 跨域配置
│   └── JpaConfig.java   # JPA配置
├── entity/              # 实体类
├── repository/          # JPA Repository
├── service/             # 服务接口
│   └── impl/            # 服务实现
├── controller/          # 控制器
└── dto/                 # 数据传输对象
```

### 前端模块结构

```
src/
├── api/                 # API 接口封装
│   ├── user.js          # 用户相关API
│   ├── shop.js          # 店铺相关API
│   ├── product.js       # 商品相关API
│   ├── order.js         # 订单相关API
│   ├── cart.js          # 购物车API
│   ├── pet.js           # 宠物档案API
│   ├── community.js     # 社区API
│   ├── coupon.js        # 优惠券API
│   └── admin.js         # 管理员API
├── store/               # Pinia 状态管理
│   ├── user.js          # 用户状态
│   ├── cart.js          # 购物车状态
│   └── app.js           # 应用全局状态
├── router/              # 路由配置
│   ├── index.js         # 主路由
│   ├── user.js          # 用户端路由
│   ├── shop.js          # 店家端路由
│   └── admin.js         # 管理端路由
├── views/               # 页面组件
│   ├── user/            # 用户端页面
│   ├── shop/            # 店家端页面
│   ├── admin/           # 管理端页面
│   └── community/       # 社区页面
├── components/          # 公共组件
│   ├── ProductCard.vue  # 商品卡片
│   ├── NavBar.vue       # 导航栏
│   └── Footer.vue       # 页脚
└── utils/               # 工具函数
    ├── request.js       # Axios 封装
    └── auth.js          # 认证工具
```

## 数据模型设计

### 核心实体关系图

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    User     │1    n │     Pet     │       │    Shop     │
│  (用户)     │───────│  (宠物档案)  │       │   (店铺)    │
└─────────────┘       └─────────────┘       └─────────────┘
      │1                                            │1
      │                                             │
      │n                                            │n
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    Order    │1    n │ OrderItem   │n    1 │   Product   │
│   (订单)    │───────│ (订单明细)  │───────│   (商品)    │
└─────────────┘       └─────────────┘       └─────────────┘
                                                    │n
                                                    │
                                                    │1
                                            ┌─────────────┐
                                            │  Category   │
                                            │  (分类)     │
                                            └─────────────┘
```

### 数据库表设计

#### 1. 用户表 (user)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(50) | 用户名 | UNIQUE, NOT NULL |
| password | VARCHAR(255) | 密码（明文） | NOT NULL |
| nickname | VARCHAR(50) | 昵称 | |
| email | VARCHAR(100) | 邮箱 | |
| phone | VARCHAR(20) | 手机号 | |
| avatar | VARCHAR(255) | 头像URL | |
| address | VARCHAR(255) | 地址 | |
| role | VARCHAR(20) | 角色(USER/SHOP/ADMIN) | NOT NULL |
| status | INT | 状态(0禁用/1启用) | DEFAULT 1 |
| create_time | DATETIME | 创建时间 | |
| update_time | DATETIME | 更新时间 | |

#### 2. 宠物档案表 (pet)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | FOREIGN KEY |
| name | VARCHAR(50) | 宠物名称 | NOT NULL |
| species | VARCHAR(50) | 种类 | |
| age | INT | 年龄 | |
| gender | VARCHAR(10) | 性别 | |
| avatar | VARCHAR(255) | 头像URL | |
| create_time | DATETIME | 创建时间 | |

#### 3. 店铺表 (shop)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 店主用户ID | FOREIGN KEY |
| name | VARCHAR(100) | 店铺名称 | NOT NULL |
| description | TEXT | 店铺描述 | |
| logo | VARCHAR(255) | 店铺Logo | |
| contact | VARCHAR(100) | 联系方式 | |
| status | INT | 状态(0待审核/1正常/2禁用) | DEFAULT 0 |
| create_time | DATETIME | 创建时间 | |
| update_time | DATETIME | 更新时间 | |

#### 4. 商品分类表 (category)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(50) | 分类名称 | NOT NULL |
| parent_id | BIGINT | 父分类ID | |
| sort_order | INT | 排序 | DEFAULT 0 |
| create_time | DATETIME | 创建时间 | |

#### 5. 商品表 (product)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| shop_id | BIGINT | 店铺ID | FOREIGN KEY |
| category_id | BIGINT | 分类ID | FOREIGN KEY |
| name | VARCHAR(100) | 商品名称 | NOT NULL |
| description | TEXT | 商品描述 | |
| price | DECIMAL(10,2) | 价格 | NOT NULL |
| stock | INT | 库存 | DEFAULT 0 |
| image | VARCHAR(255) | 主图URL | |
| images | TEXT | 商品图片(JSON) | |
| status | INT | 状态(0下架/1上架/2待审核) | DEFAULT 2 |
| sales | INT | 销量 | DEFAULT 0 |
| create_time | DATETIME | 创建时间 | |
| update_time | DATETIME | 更新时间 | |

#### 6. 购物车表 (cart)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | FOREIGN KEY |
| product_id | BIGINT | 商品ID | FOREIGN KEY |
| quantity | INT | 数量 | NOT NULL |
| create_time | DATETIME | 创建时间 | |
| update_time | DATETIME | 更新时间 | |

#### 7. 订单表 (orders)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| order_no | VARCHAR(50) | 订单号 | UNIQUE, NOT NULL |
| user_id | BIGINT | 用户ID | FOREIGN KEY |
| shop_id | BIGINT | 店铺ID | FOREIGN KEY |
| total_amount | DECIMAL(10,2) | 总金额 | NOT NULL |
| status | INT | 状态(0待支付/1待发货/2已发货/3已完成/4已取消) | DEFAULT 0 |
| receiver_name | VARCHAR(50) | 收货人 | |
| receiver_phone | VARCHAR(20) | 收货电话 | |
| receiver_address | VARCHAR(255) | 收货地址 | |
| remark | VARCHAR(255) | 备注 | |
| pay_time | DATETIME | 支付时间 | |
| ship_time | DATETIME | 发货时间 | |
| create_time | DATETIME | 创建时间 | |
| update_time | DATETIME | 更新时间 | |

#### 8. 订单明细表 (order_item)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| order_id | BIGINT | 订单ID | FOREIGN KEY |
| product_id | BIGINT | 商品ID | FOREIGN KEY |
| product_name | VARCHAR(100) | 商品名称 | |
| product_image | VARCHAR(255) | 商品图片 | |
| price | DECIMAL(10,2) | 单价 | NOT NULL |
| quantity | INT | 数量 | NOT NULL |
| subtotal | DECIMAL(10,2) | 小计 | NOT NULL |

#### 9. 商品评价表 (review)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | FOREIGN KEY |
| product_id | BIGINT | 商品ID | FOREIGN KEY |
| order_id | BIGINT | 订单ID | FOREIGN KEY |
| rating | INT | 评分(1-5) | NOT NULL |
| content | TEXT | 评价内容 | |
| images | TEXT | 评价图片(JSON) | |
| create_time | DATETIME | 创建时间 | |

#### 10. 社区帖子表 (community_post)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | FOREIGN KEY |
| title | VARCHAR(100) | 标题 | NOT NULL |
| content | TEXT | 内容 | NOT NULL |
| images | TEXT | 图片(JSON) | |
| likes | INT | 点赞数 | DEFAULT 0 |
| views | INT | 浏览数 | DEFAULT 0 |
| status | INT | 状态(0正常/1已删除) | DEFAULT 0 |
| create_time | DATETIME | 创建时间 | |
| update_time | DATETIME | 更新时间 | |

#### 11. 社区评论表 (community_reply)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| post_id | BIGINT | 帖子ID | FOREIGN KEY |
| user_id | BIGINT | 用户ID | FOREIGN KEY |
| content | TEXT | 评论内容 | NOT NULL |
| create_time | DATETIME | 创建时间 | |

#### 12. 帖子点赞表 (post_like)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| post_id | BIGINT | 帖子ID | FOREIGN KEY |
| user_id | BIGINT | 用户ID | FOREIGN KEY |
| create_time | DATETIME | 创建时间 | |
| UNIQUE(post_id, user_id) | | 联合唯一索引 | |

#### 13. 优惠券表 (coupon)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| shop_id | BIGINT | 店铺ID | FOREIGN KEY |
| name | VARCHAR(100) | 优惠券名称 | NOT NULL |
| discount_amount | DECIMAL(10,2) | 折扣金额 | NOT NULL |
| min_amount | DECIMAL(10,2) | 最低消费金额 | DEFAULT 0 |
| total_count | INT | 发放总数 | |
| used_count | INT | 已使用数量 | DEFAULT 0 |
| start_time | DATETIME | 开始时间 | |
| end_time | DATETIME | 结束时间 | |
| status | INT | 状态(0禁用/1启用) | DEFAULT 1 |
| create_time | DATETIME | 创建时间 | |

#### 14. 用户优惠券表 (user_coupon)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | FOREIGN KEY |
| coupon_id | BIGINT | 优惠券ID | FOREIGN KEY |
| order_id | BIGINT | 使用的订单ID | |
| status | INT | 状态(0未使用/1已使用/2已过期) | DEFAULT 0 |
| receive_time | DATETIME | 领取时间 | |
| use_time | DATETIME | 使用时间 | |

#### 15. 浏览历史表 (browse_history)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | FOREIGN KEY |
| product_id | BIGINT | 商品ID | FOREIGN KEY |
| create_time | DATETIME | 浏览时间 | |

## 组件和接口设计

### 统一响应格式

所有API接口返回统一的JSON格式：

```java
public class Result<T> {
    private Integer code;      // 状态码
    private String message;    // 消息
    private T data;           // 数据
}
```

状态码定义：
- 200: 成功
- 400: 请求参数错误
- 401: 未授权
- 403: 禁止访问
- 404: 资源不存在
- 500: 服务器内部错误

### RESTful API 设计规范

#### 用户模块 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/user/register | 用户注册 |
| POST | /api/user/login | 用户登录 |
| GET | /api/user/profile | 获取用户信息 |
| PUT | /api/user/profile | 更新用户信息 |
| POST | /api/user/avatar | 上传头像 |

#### 宠物档案模块 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/pet | 创建宠物档案 |
| GET | /api/pet/list | 获取宠物列表 |
| GET | /api/pet/{id} | 获取宠物详情 |
| PUT | /api/pet/{id} | 更新宠物信息 |
| DELETE | /api/pet/{id} | 删除宠物档案 |

#### 商品模块 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/product/list | 获取商品列表(分页) |
| GET | /api/product/{id} | 获取商品详情 |
| GET | /api/product/search | 搜索商品 |
| GET | /api/product/category/{id} | 按分类获取商品 |
| GET | /api/product/recommend | 获取推荐商品 |
| POST | /api/product | 创建商品(店家) |
| PUT | /api/product/{id} | 更新商品(店家) |
| DELETE | /api/product/{id} | 删除商品(店家) |
| PUT | /api/product/{id}/status | 上下架商品(店家) |

#### 购物车模块 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/cart | 添加到购物车 |
| GET | /api/cart/list | 获取购物车列表 |
| PUT | /api/cart/{id} | 更新购物车商品数量 |
| DELETE | /api/cart/{id} | 删除购物车商品 |
| DELETE | /api/cart/clear | 清空购物车 |

#### 订单模块 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/order | 创建订单 |
| GET | /api/order/list | 获取订单列表 |
| GET | /api/order/{id} | 获取订单详情 |
| PUT | /api/order/{id}/pay | 支付订单 |
| PUT | /api/order/{id}/cancel | 取消订单 |
| PUT | /api/order/{id}/ship | 发货(店家) |
| PUT | /api/order/{id}/complete | 确认收货 |

#### 评价模块 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/review | 创建评价 |
| GET | /api/review/product/{id} | 获取商品评价列表 |
| GET | /api/review/user | 获取用户评价列表 |

#### 社区模块 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/community/post | 发布帖子 |
| GET | /api/community/post/list | 获取帖子列表 |
| GET | /api/community/post/{id} | 获取帖子详情 |
| DELETE | /api/community/post/{id} | 删除帖子 |
| POST | /api/community/reply | 发布评论 |
| GET | /api/community/reply/{postId} | 获取帖子评论 |
| POST | /api/community/like/{postId} | 点赞/取消点赞 |

#### 店铺模块 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/shop | 创建店铺 |
| GET | /api/shop/{id} | 获取店铺信息 |
| PUT | /api/shop/{id} | 更新店铺信息 |
| GET | /api/shop/{id}/products | 获取店铺商品列表 |

#### 优惠券模块 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/coupon | 创建优惠券(店家) |
| GET | /api/coupon/list | 获取优惠券列表(店家) |
| GET | /api/coupon/shop/{shopId} | 获取店铺可用优惠券 |
| POST | /api/coupon/{id}/receive | 领取优惠券 |
| GET | /api/coupon/user | 获取用户优惠券 |

#### 管理员模块 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/user/list | 获取用户列表 |
| PUT | /api/admin/user/{id}/status | 启用/禁用用户 |
| GET | /api/admin/shop/list | 获取店铺列表 |
| PUT | /api/admin/shop/{id}/audit | 审核店铺 |
| PUT | /api/admin/shop/{id}/status | 启用/禁用店铺 |
| GET | /api/admin/product/list | 获取待审核商品 |
| PUT | /api/admin/product/{id}/audit | 审核商品 |
| GET | /api/admin/post/list | 获取帖子列表 |
| DELETE | /api/admin/post/{id} | 删除帖子 |
| DELETE | /api/admin/reply/{id} | 删除评论 |
| GET | /api/admin/dashboard | 获取数据看板 |

## 核心业务逻辑设计

### 用户认证流程

1. 用户提交登录表单（用户名 + 密码）
2. 后端验证用户名和密码（明文比对）
3. 验证成功后生成会话标识（可使用简单的UUID）
4. 返回用户信息和会话标识给前端
5. 前端将会话标识存储在 localStorage
6. 后续请求在 Header 中携带会话标识

### 商品推荐算法（简化版）

**基于宠物种类的推荐：**
1. 获取用户的宠物档案
2. 根据宠物种类（猫/狗/其他）筛选对应分类的商品
3. 按销量排序返回前10个商品

**基于浏览历史的推荐：**
1. 获取用户最近浏览的商品
2. 找出这些商品的分类
3. 推荐相同分类下的其他热门商品

### 订单创建流程

1. 用户从购物车选择商品提交订单
2. 验证商品库存是否充足
3. 计算订单总金额（含优惠券折扣）
4. 创建订单记录和订单明细
5. 扣减商品库存
6. 清空购物车中已下单的商品
7. 返回订单信息

### 库存管理策略

- 下单时扣减库存（而非支付时）
- 取消订单时恢复库存
- 库存为0时商品自动标记为缺货状态
- 店家可手动调整库存数量

## 错误处理

### 全局异常处理

使用 `@ControllerAdvice` 统一处理异常：

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        return Result.error(500, "系统错误");
    }
}
```

### 自定义业务异常

```java
public class BusinessException extends RuntimeException {
    private Integer code;
    private String message;
}
```

### 常见错误场景

- 用户名已存在
- 用户名或密码错误
- 商品库存不足
- 订单不存在
- 无权限操作
- 数据验证失败

## 前端状态管理

### User Store (Pinia)

```javascript
export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    token: null,
    isLogin: false
  }),
  actions: {
    login(userInfo, token) {
      this.userInfo = userInfo
      this.token = token
      this.isLogin = true
      localStorage.setItem('token', token)
    },
    logout() {
      this.userInfo = null
      this.token = null
      this.isLogin = false
      localStorage.removeItem('token')
    }
  }
})
```

### Cart Store (Pinia)

```javascript
export const useCartStore = defineStore('cart', {
  state: () => ({
    cartItems: [],
    totalCount: 0,
    totalAmount: 0
  }),
  actions: {
    async fetchCart() {
      // 从后端获取购物车数据
    },
    addToCart(product, quantity) {
      // 添加商品到购物车
    },
    removeFromCart(cartId) {
      // 从购物车移除商品
    }
  }
})
```

## 前端路由设计

### 用户端路由

```javascript
{
  path: '/',
  component: Layout,
  children: [
    { path: '', component: Home },                    // 首页
    { path: 'products', component: ProductList },     // 商品列表
    { path: 'product/:id', component: ProductDetail }, // 商品详情
    { path: 'cart', component: Cart },                // 购物车
    { path: 'orders', component: OrderList },         // 订单列表
    { path: 'order/:id', component: OrderDetail },    // 订单详情
    { path: 'profile', component: Profile },          // 个人中心
    { path: 'pets', component: PetList },             // 宠物档案
    { path: 'community', component: CommunityList },  // 社区列表
    { path: 'post/:id', component: PostDetail },      // 帖子详情
  ]
}
```

### 店家端路由

```javascript
{
  path: '/shop',
  component: ShopLayout,
  children: [
    { path: 'dashboard', component: ShopDashboard },  // 店铺首页
    { path: 'info', component: ShopInfo },            // 店铺信息
    { path: 'products', component: ShopProducts },    // 商品管理
    { path: 'orders', component: ShopOrders },        // 订单管理
    { path: 'coupons', component: ShopCoupons },      // 优惠券管理
  ]
}
```

### 管理端路由

```javascript
{
  path: '/admin',
  component: AdminLayout,
  children: [
    { path: 'dashboard', component: AdminDashboard }, // 数据看板
    { path: 'users', component: UserManage },         // 用户管理
    { path: 'shops', component: ShopManage },         // 店铺管理
    { path: 'products', component: ProductAudit },    // 商品审核
    { path: 'posts', component: PostManage },         // 社区管理
  ]
}
```

## 前端组件设计

### 公共组件

**ProductCard.vue** - 商品卡片组件
- Props: product (商品对象)
- 显示：商品图片、名称、价格、销量
- 事件：点击跳转到商品详情

**NavBar.vue** - 导航栏组件
- 显示：Logo、搜索框、购物车图标、用户头像
- 功能：搜索、跳转购物车、用户菜单

**Pagination.vue** - 分页组件
- Props: total, pageSize, currentPage
- 事件：页码变化

**ImageUpload.vue** - 图片上传组件
- 支持单图/多图上传
- 图片预览
- 删除图片

### 页面组件

**Home.vue** - 首页
- 轮播图
- 商品分类导航
- 推荐商品列表
- 热门商品

**ProductList.vue** - 商品列表页
- 分类筛选
- 价格排序
- 商品卡片网格
- 分页

**ProductDetail.vue** - 商品详情页
- 商品图片轮播
- 商品信息展示
- 加入购物车按钮
- 商品评价列表

**Cart.vue** - 购物车页
- 购物车商品列表
- 数量调整
- 删除商品
- 结算按钮

**OrderList.vue** - 订单列表页
- 订单状态筛选
- 订单卡片列表
- 订单操作（支付、取消、确认收货）

## 配置文件

### application.yml (后端)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/petshop?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
  
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

server:
  port: 8080
```

### CORS 配置

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### Axios 配置 (前端)

```javascript
// utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
```

## 测试策略

### 后端测试

**单元测试：**
- Service 层业务逻辑测试
- Repository 层数据访问测试
- 使用 JUnit 5 和 Mockito

**集成测试：**
- Controller 层 API 测试
- 使用 MockMvc 模拟 HTTP 请求

### 前端测试

**组件测试：**
- 使用 Vitest 测试组件渲染
- 测试用户交互行为

**E2E 测试：**
- 使用 Cypress 测试关键业务流程
- 测试用户注册、登录、下单等流程

## 部署方案

### 开发环境

**后端：**
- IDE: IntelliJ IDEA
- 运行: Spring Boot 内置 Tomcat
- 端口: 8080

**前端：**
- IDE: VS Code
- 运行: Vite Dev Server
- 端口: 5173

**数据库：**
- MySQL 8.0
- 端口: 3306

### 生产环境（建议）

**后端：**
- 打包: Maven package 生成 JAR
- 部署: java -jar 运行或使用 Docker

**前端：**
- 打包: npm run build 生成静态文件
- 部署: Nginx 托管静态资源

**数据库：**
- 独立 MySQL 服务器
- 定期备份数据

## 性能优化

### 后端优化

1. **数据库优化**
   - 为常用查询字段添加索引
   - 使用 JPA 的懒加载避免 N+1 问题
   - 分页查询避免一次性加载大量数据

2. **缓存策略**
   - 商品分类数据缓存（变化频率低）
   - 热门商品列表缓存
   - 可使用 Spring Cache 或 Redis

3. **接口优化**
   - 使用 DTO 减少数据传输量
   - 避免返回不必要的关联数据

### 前端优化

1. **资源优化**
   - 图片懒加载
   - 路由懒加载
   - 组件按需引入

2. **性能优化**
   - 使用虚拟滚动处理长列表
   - 防抖和节流优化搜索和滚动事件
   - 使用 keep-alive 缓存页面组件

3. **用户体验**
   - 骨架屏加载效果
   - 加载状态提示
   - 错误边界处理

## 安全考虑

### 后端安全

1. **输入验证**
   - 使用 Hibernate Validator 验证请求参数
   - 防止 SQL 注入（JPA 自动处理）
   - XSS 防护：对用户输入进行转义

2. **权限控制**
   - 接口级别的角色验证
   - 用户只能操作自己的数据
   - 店家只能管理自己的店铺和商品

3. **数据安全**
   - 虽然密码明文存储（毕设要求），但生产环境应使用加密
   - 敏感信息不在日志中输出

### 前端安全

1. **Token 管理**
   - Token 存储在 localStorage
   - 请求失败时清除 Token
   - 定期刷新 Token（可选）

2. **路由守卫**
   - 未登录用户重定向到登录页
   - 根据角色控制路由访问权限

## 开发规范

### 代码规范

**后端：**
- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- 所有类和方法添加中文注释

**前端：**
- 遵循 Vue 3 官方风格指南
- 使用 ESLint 检查代码质量
- 组件命名使用 PascalCase

### Git 提交规范

- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关

### 接口文档

使用 Swagger/OpenAPI 自动生成接口文档，方便前后端协作。

## 项目里程碑

1. **第一阶段：基础框架搭建**
   - 后端项目结构
   - 前端项目结构
   - 数据库设计

2. **第二阶段：核心功能开发**
   - 用户模块
   - 商品模块
   - 订单模块

3. **第三阶段：扩展功能开发**
   - 社区模块
   - 店家管理
   - 管理员功能

4. **第四阶段：测试和优化**
   - 功能测试
   - 性能优化
   - Bug 修复

5. **第五阶段：部署上线**
   - 生产环境部署
   - 文档完善
