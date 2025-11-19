# API 文档

星落宠物用品销售网站 RESTful API 完整文档。

## 基础信息

- **基础 URL**：`http://localhost:8080/api`
- **请求格式**：JSON
- **响应格式**：JSON
- **字符编码**：UTF-8

## 统一响应格式

所有 API 返回统一的 JSON 格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 状态码说明

| 状态码 | 说明 | 处理方式 |
|--------|------|---------|
| 200 | 请求成功 | 正常处理 |
| 400 | 请求参数错误 | 检查参数 |
| 401 | 未授权（未登录） | 跳转到登录页 |
| 403 | 禁止访问（无权限） | 显示无权限提示 |
| 404 | 资源不存在 | 显示 404 页面 |
| 500 | 服务器内部错误 | 显示错误提示 |

## 用户模块 API

### 1. 用户注册

**请求方式**：POST

**请求路径**：`/user/register`

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名（3-50字符） |
| password | String | 是 | 密码（6-20字符） |
| email | String | 否 | 邮箱 |
| phone | String | 否 | 手机号 |

**请求示例**：

```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123",
    "email": "test@example.com",
    "phone": "13800000000"
  }'
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "testuser",
    "email": "test@example.com",
    "phone": "13800000000",
    "role": "USER",
    "status": 1
  }
}
```

**错误响应**：

```json
{
  "code": 400,
  "message": "用户名已被使用",
  "data": null
}
```

### 2. 用户登录

**请求方式**：POST

**请求路径**：`/user/login`

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**请求示例**：

```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123"
  }'
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "testuser",
    "email": "test@example.com",
    "phone": "13800000000",
    "avatar": "https://example.com/avatar.jpg",
    "address": "北京市朝阳区",
    "role": "USER",
    "status": 1,
    "token": "user_session_token_xxx"
  }
}
```

### 3. 获取用户信息

**请求方式**：GET

**请求路径**：`/user/profile`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl http://localhost:8080/api/user/profile \
  -H "Authorization: user_token"
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "testuser",
    "email": "test@example.com",
    "phone": "13800000000",
    "avatar": "https://example.com/avatar.jpg",
    "address": "北京市朝阳区",
    "role": "USER",
    "status": 1
  }
}
```

### 4. 更新用户信息

**请求方式**：PUT

**请求路径**：`/user/profile`

**请求头**：

```
Authorization: user_token
Content-Type: application/json
```

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| nickname | String | 否 | 昵称 |
| email | String | 否 | 邮箱 |
| phone | String | 否 | 手机号 |
| avatar | String | 否 | 头像 URL |
| address | String | 否 | 地址 |

**请求示例**：

```bash
curl -X PUT http://localhost:8080/api/user/profile \
  -H "Authorization: user_token" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "新昵称",
    "phone": "13900000000",
    "address": "上海市浦东新区"
  }'
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "新昵称",
    "email": "test@example.com",
    "phone": "13900000000",
    "avatar": "https://example.com/avatar.jpg",
    "address": "上海市浦东新区",
    "role": "USER",
    "status": 1
  }
}
```

## 商品模块 API

### 1. 获取商品列表

**请求方式**：GET

**请求路径**：`/product/list`

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码（默认1） |
| pageSize | Integer | 否 | 每页数量（默认10） |

**请求示例**：

```bash
curl "http://localhost:8080/api/product/list?page=1&pageSize=10"
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "name": "皇家狗粮 - 成犬粮 2kg",
        "price": 89.90,
        "image": "https://example.com/product1.jpg",
        "stock": 100,
        "sales": 50,
        "status": 1
      }
    ]
  }
}
```

### 2. 搜索商品

**请求方式**：GET

**请求路径**：`/product/search`

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| keyword | String | 是 | 搜索关键词 |
| page | Integer | 否 | 页码（默认1） |
| pageSize | Integer | 否 | 每页数量（默认10） |

**请求示例**：

```bash
curl "http://localhost:8080/api/product/search?keyword=狗粮&page=1&pageSize=10"
```

### 3. 获取商品详情

**请求方式**：GET

**请求路径**：`/product/{id}`

**请求示例**：

```bash
curl http://localhost:8080/api/product/1
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "皇家狗粮 - 成犬粮 2kg",
    "description": "高品质狗粮，营养均衡，适合成年犬",
    "price": 89.90,
    "stock": 100,
    "image": "https://example.com/product1.jpg",
    "images": ["https://example.com/product1.jpg", "https://example.com/product1_2.jpg"],
    "sales": 50,
    "status": 1,
    "shop": {
      "id": 1,
      "name": "宠物用品旗舰店",
      "logo": "https://example.com/shop1.jpg"
    },
    "category": {
      "id": 1,
      "name": "狗粮"
    }
  }
}
```

### 4. 按分类获取商品

**请求方式**：GET

**请求路径**：`/product/category/{categoryId}`

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码（默认1） |
| pageSize | Integer | 否 | 每页数量（默认10） |

**请求示例**：

```bash
curl "http://localhost:8080/api/product/category/1?page=1&pageSize=10"
```

### 5. 获取推荐商品

**请求方式**：GET

**请求路径**：`/product/recommend`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl http://localhost:8080/api/product/recommend \
  -H "Authorization: user_token"
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "皇家狗粮 - 成犬粮 2kg",
      "price": 89.90,
      "image": "https://example.com/product1.jpg",
      "stock": 100,
      "sales": 50
    }
  ]
}
```

## 购物车模块 API

### 1. 添加到购物车

**请求方式**：POST

**请求路径**：`/cart`

**请求头**：

```
Authorization: user_token
Content-Type: application/json
```

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| productId | Long | 是 | 商品 ID |
| quantity | Integer | 是 | 数量 |

**请求示例**：

```bash
curl -X POST http://localhost:8080/api/cart \
  -H "Authorization: user_token" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "productId": 1,
    "quantity": 2,
    "product": {
      "id": 1,
      "name": "皇家狗粮 - 成犬粮 2kg",
      "price": 89.90,
      "image": "https://example.com/product1.jpg"
    }
  }
}
```

### 2. 获取购物车列表

**请求方式**：GET

**请求路径**：`/cart/list`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl http://localhost:8080/api/cart/list \
  -H "Authorization: user_token"
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "productId": 1,
      "quantity": 2,
      "product": {
        "id": 1,
        "name": "皇家狗粮 - 成犬粮 2kg",
        "price": 89.90,
        "image": "https://example.com/product1.jpg"
      }
    }
  ]
}
```

### 3. 更新购物车商品数量

**请求方式**：PUT

**请求路径**：`/cart/{id}`

**请求头**：

```
Authorization: user_token
Content-Type: application/json
```

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| quantity | Integer | 是 | 新数量 |

**请求示例**：

```bash
curl -X PUT http://localhost:8080/api/cart/1 \
  -H "Authorization: user_token" \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 3
  }'
```

### 4. 删除购物车商品

**请求方式**：DELETE

**请求路径**：`/cart/{id}`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl -X DELETE http://localhost:8080/api/cart/1 \
  -H "Authorization: user_token"
```

### 5. 清空购物车

**请求方式**：DELETE

**请求路径**：`/cart/clear`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl -X DELETE http://localhost:8080/api/cart/clear \
  -H "Authorization: user_token"
```

## 订单模块 API

### 1. 创建订单

**请求方式**：POST

**请求路径**：`/order`

**请求头**：

```
Authorization: user_token
Content-Type: application/json
```

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| cartIds | List | 是 | 购物车 ID 列表 |
| receiverName | String | 是 | 收货人 |
| receiverPhone | String | 是 | 收货电话 |
| receiverAddress | String | 是 | 收货地址 |
| couponId | Long | 否 | 优惠券 ID |

**请求示例**：

```bash
curl -X POST http://localhost:8080/api/order \
  -H "Authorization: user_token" \
  -H "Content-Type: application/json" \
  -d '{
    "cartIds": [1, 2],
    "receiverName": "张三",
    "receiverPhone": "13800000000",
    "receiverAddress": "北京市朝阳区建国路1号"
  }'
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "orderNo": "ORD20241119001",
    "totalAmount": 179.80,
    "status": 0,
    "receiverName": "张三",
    "receiverPhone": "13800000000",
    "receiverAddress": "北京市朝阳区建国路1号",
    "items": [
      {
        "id": 1,
        "productName": "皇家狗粮 - 成犬粮 2kg",
        "price": 89.90,
        "quantity": 2,
        "subtotal": 179.80
      }
    ]
  }
}
```

### 2. 获取订单列表

**请求方式**：GET

**请求路径**：`/order/list`

**请求头**：

```
Authorization: user_token
```

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| status | Integer | 否 | 订单状态 |
| page | Integer | 否 | 页码（默认1） |
| pageSize | Integer | 否 | 每页数量（默认10） |

**请求示例**：

```bash
curl "http://localhost:8080/api/order/list?page=1&pageSize=10" \
  -H "Authorization: user_token"
```

### 3. 获取订单详情

**请求方式**：GET

**请求路径**：`/order/{id}`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl http://localhost:8080/api/order/1 \
  -H "Authorization: user_token"
```

### 4. 支付订单

**请求方式**：PUT

**请求路径**：`/order/{id}/pay`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl -X PUT http://localhost:8080/api/order/1/pay \
  -H "Authorization: user_token"
```

### 5. 取消订单

**请求方式**：PUT

**请求路径**：`/order/{id}/cancel`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl -X PUT http://localhost:8080/api/order/1/cancel \
  -H "Authorization: user_token"
```

### 6. 确认收货

**请求方式**：PUT

**请求路径**：`/order/{id}/complete`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl -X PUT http://localhost:8080/api/order/1/complete \
  -H "Authorization: user_token"
```

## 社区模块 API

### 1. 发布帖子

**请求方式**：POST

**请求路径**：`/community/post`

**请求头**：

```
Authorization: user_token
Content-Type: application/json
```

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| title | String | 是 | 标题 |
| content | String | 是 | 内容 |
| images | List | 否 | 图片 URL 列表 |

**请求示例**：

```bash
curl -X POST http://localhost:8080/api/community/post \
  -H "Authorization: user_token" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "我的宠物日常",
    "content": "分享我的宠物生活"
  }'
```

### 2. 获取帖子列表

**请求方式**：GET

**请求路径**：`/community/post/list`

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码（默认1） |
| pageSize | Integer | 否 | 每页数量（默认10） |

**请求示例**：

```bash
curl "http://localhost:8080/api/community/post/list?page=1&pageSize=10"
```

### 3. 获取帖子详情

**请求方式**：GET

**请求路径**：`/community/post/{id}`

**请求示例**：

```bash
curl http://localhost:8080/api/community/post/1
```

### 4. 发布评论

**请求方式**：POST

**请求路径**：`/community/reply`

**请求头**：

```
Authorization: user_token
Content-Type: application/json
```

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| postId | Long | 是 | 帖子 ID |
| content | String | 是 | 评论内容 |

**请求示例**：

```bash
curl -X POST http://localhost:8080/api/community/reply \
  -H "Authorization: user_token" \
  -H "Content-Type: application/json" \
  -d '{
    "postId": 1,
    "content": "很好的分享！"
  }'
```

### 5. 获取帖子评论

**请求方式**：GET

**请求路径**：`/community/reply/{postId}`

**请求示例**：

```bash
curl http://localhost:8080/api/community/reply/1
```

### 6. 点赞/取消点赞

**请求方式**：POST

**请求路径**：`/community/like/{postId}`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl -X POST http://localhost:8080/api/community/like/1 \
  -H "Authorization: user_token"
```

## 宠物档案模块 API

### 1. 创建宠物档案

**请求方式**：POST

**请求路径**：`/pet`

**请求头**：

```
Authorization: user_token
Content-Type: application/json
```

**请求参数**：

| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| name | String | 是 | 宠物名称 |
| species | String | 是 | 种类（狗/猫/其他） |
| age | Integer | 否 | 年龄 |
| gender | String | 否 | 性别（公/母） |

**请求示例**：

```bash
curl -X POST http://localhost:8080/api/pet \
  -H "Authorization: user_token" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "小白",
    "species": "狗",
    "age": 2,
    "gender": "公"
  }'
```

### 2. 获取宠物列表

**请求方式**：GET

**请求路径**：`/pet/list`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl http://localhost:8080/api/pet/list \
  -H "Authorization: user_token"
```

### 3. 更新宠物信息

**请求方式**：PUT

**请求路径**：`/pet/{id}`

**请求头**：

```
Authorization: user_token
Content-Type: application/json
```

**请求示例**：

```bash
curl -X PUT http://localhost:8080/api/pet/1 \
  -H "Authorization: user_token" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "小白",
    "age": 3
  }'
```

### 4. 删除宠物档案

**请求方式**：DELETE

**请求路径**：`/pet/{id}`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl -X DELETE http://localhost:8080/api/pet/1 \
  -H "Authorization: user_token"
```

## 优惠券模块 API

### 1. 获取店铺可用优惠券

**请求方式**：GET

**请求路径**：`/coupon/shop/{shopId}`

**请求示例**：

```bash
curl http://localhost:8080/api/coupon/shop/1
```

### 2. 领取优惠券

**请求方式**：POST

**请求路径**：`/coupon/{id}/receive`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl -X POST http://localhost:8080/api/coupon/1/receive \
  -H "Authorization: user_token"
```

### 3. 获取用户优惠券

**请求方式**：GET

**请求路径**：`/coupon/user`

**请求头**：

```
Authorization: user_token
```

**请求示例**：

```bash
curl http://localhost:8080/api/coupon/user \
  -H "Authorization: user_token"
```

## 错误处理

### 常见错误响应

#### 未授权错误

```json
{
  "code": 401,
  "message": "未登录或登录已过期",
  "data": null
}
```

#### 参数错误

```json
{
  "code": 400,
  "message": "参数验证失败：username 不能为空",
  "data": null
}
```

#### 资源不存在

```json
{
  "code": 404,
  "message": "商品不存在",
  "data": null
}
```

#### 服务器错误

```json
{
  "code": 500,
  "message": "系统错误，请稍后重试",
  "data": null
}
```

## 分页说明

所有列表接口都支持分页，使用以下参数：

| 参数名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| page | Integer | 1 | 页码 |
| pageSize | Integer | 10 | 每页数量 |

分页响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "list": []
  }
}
```

## 认证说明

大多数 API 需要在请求头中携带 Authorization token：

```
Authorization: user_token
```

Token 通过登录接口获取，有效期为 24 小时。

## 速率限制

- 每个 IP 每分钟最多 60 个请求
- 每个用户每分钟最多 100 个请求

超过限制将返回 429 状态码。

## 支持

如有问题，请参考：
- 项目 README：README.md
- 设计文档：doc/设计文档.md
