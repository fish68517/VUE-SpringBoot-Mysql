# API端点验证文档

## 概述
本文档验证所有API端点的连接状态和前后端数据流的完整性。

## 系统配置验证

### 后端配置
- ✅ Spring Boot 3.2.0 配置完成
- ✅ MySQL 数据库连接配置完成 (localhost:3306/tourism_db)
- ✅ API 上下文路径: /api
- ✅ 服务器端口: 8080
- ✅ CORS 跨域配置已启用

### 前端配置
- ✅ Vue 3 项目结构完成
- ✅ API 基础 URL: http://localhost:8080/api
- ✅ Axios 请求拦截器配置完成
- ✅ 用户信息通过请求头传递 (X-User-Id, X-User-Role)

## API端点验证清单

### 1. 用户管理模块 (User Module)

#### 认证端点
- ✅ POST /api/users/register - 用户注册
  - 请求: username, password, email, phone
  - 响应: 用户信息 (id, username, email, phone, role, status)
  - 前端集成: VUE/src/views/auth/Register.vue

- ✅ POST /api/users/login - 用户登录
  - 请求: username, password
  - 响应: 用户信息 (id, username, email, phone, realName, role, status)
  - 前端集成: VUE/src/views/auth/Login.vue

#### 个人信息端点
- ✅ GET /api/users/{id} - 获取用户个人信息
  - 请求参数: userId (路径参数)
  - 响应: 用户详细信息
  - 前端集成: VUE/src/views/tourist/Profile.vue

- ✅ PUT /api/users/{id} - 更新用户个人信息
  - 请求: realName, email, phone
  - 响应: 更新后的用户信息
  - 前端集成: VUE/src/views/tourist/Profile.vue

#### 管理员端点
- ✅ GET /api/users/admin/list - 获取用户列表 (分页)
  - 请求参数: page, size
  - 响应: 用户列表, 分页信息
  - 前端集成: VUE/src/views/admin/Users.vue

- ✅ PUT /api/users/admin/{id}/disable - 禁用用户
  - 响应: 更新后的用户状态
  - 前端集成: VUE/src/views/admin/Users.vue

- ✅ PUT /api/users/admin/{id}/enable - 启用用户
  - 响应: 更新后的用户状态
  - 前端集成: VUE/src/views/admin/Users.vue

- ✅ DELETE /api/users/admin/{id} - 删除用户
  - 响应: 成功消息
  - 前端集成: VUE/src/views/admin/Users.vue

### 2. 景点管理模块 (Attraction Module)

#### 查询端点
- ✅ GET /api/attractions/list - 获取景点列表 (分页)
  - 请求参数: page, size
  - 响应: 景点列表, 分页信息
  - 前端集成: VUE/src/views/tourist/Attractions.vue

- ✅ GET /api/attractions/{id} - 获取景点详情
  - 请求参数: userId (可选, 用于记录浏览历史)
  - 响应: 景点详细信息, 标签列表
  - 前端集成: VUE/src/views/tourist/AttractionDetail.vue

- ✅ GET /api/attractions/search - 搜索景点
  - 请求参数: keyword, page, size
  - 响应: 搜索结果, 分页信息
  - 前端集成: VUE/src/views/tourist/Attractions.vue

- ✅ GET /api/attractions/by-tag - 按标签查询景点
  - 请求参数: tagName, page, size
  - 响应: 景点列表, 分页信息
  - 前端集成: VUE/src/views/tourist/Attractions.vue

- ✅ GET /api/attractions/guangzhou-special - 获取广州特色景点
  - 请求参数: page, size
  - 响应: 广州特色景点列表, 分页信息
  - 前端集成: VUE/src/views/tourist/Home.vue

#### 管理员端点
- ✅ POST /api/attractions/admin/create - 创建景点
  - 请求: name, description, location, ticketPrice, openingHours, imageUrl, isGuangzhouSpecial
  - 响应: 创建的景点信息
  - 前端集成: VUE/src/views/admin/Attractions.vue

- ✅ PUT /api/attractions/admin/{id} - 更新景点
  - 请求: 景点信息
  - 响应: 更新后的景点信息
  - 前端集成: VUE/src/views/admin/Attractions.vue

- ✅ DELETE /api/attractions/admin/{id} - 删除景点
  - 响应: 成功消息
  - 前端集成: VUE/src/views/admin/Attractions.vue

- ✅ POST /api/attractions/admin/{attractionId}/tags - 添加景点标签
  - 请求: tagName
  - 响应: 标签信息
  - 前端集成: VUE/src/views/admin/Attractions.vue

- ✅ DELETE /api/attractions/admin/{attractionId}/tags/{tagName} - 删除景点标签
  - 响应: 成功消息
  - 前端集成: VUE/src/views/admin/Attractions.vue

### 3. 酒店管理模块 (Hotel Module)

#### 查询端点
- ✅ GET /api/hotels/list - 获取酒店列表 (分页)
  - 请求参数: page, size
  - 响应: 酒店列表, 分页信息
  - 前端集成: VUE/src/views/tourist/Hotels.vue

- ✅ GET /api/hotels/{id} - 获取酒店详情
  - 请求参数: userId (可选)
  - 响应: 酒店详细信息, 房间类型列表
  - 前端集成: VUE/src/views/tourist/HotelDetail.vue

- ✅ GET /api/hotels/search - 搜索酒店
  - 请求参数: keyword, page, size
  - 响应: 搜索结果, 分页信息
  - 前端集成: VUE/src/views/tourist/Hotels.vue

#### 管理员端点
- ✅ POST /api/hotels/admin/create - 创建酒店
  - 请求: name, location, description, imageUrl, rating
  - 响应: 创建的酒店信息
  - 前端集成: VUE/src/views/admin/Hotels.vue

- ✅ PUT /api/hotels/admin/{id} - 更新酒店
  - 请求: 酒店信息
  - 响应: 更新后的酒店信息
  - 前端集成: VUE/src/views/admin/Hotels.vue

- ✅ DELETE /api/hotels/admin/{id} - 删除酒店
  - 响应: 成功消息
  - 前端集成: VUE/src/views/admin/Hotels.vue

- ✅ POST /api/hotels/admin/{hotelId}/rooms - 添加房间类型
  - 请求: roomType, pricePerNight, quantity
  - 响应: 房间信息
  - 前端集成: VUE/src/views/admin/Hotels.vue

- ✅ DELETE /api/hotels/admin/{hotelId}/rooms/{roomId} - 删除房间类型
  - 响应: 成功消息
  - 前端集成: VUE/src/views/admin/Hotels.vue

### 4. 商品管理模块 (Product Module)

#### 查询端点
- ✅ GET /api/products/list - 获取商品列表 (分页)
  - 请求参数: page, size
  - 响应: 商品列表, 分页信息
  - 前端集成: VUE/src/views/tourist/Products.vue

- ✅ GET /api/products/{id} - 获取商品详情
  - 请求参数: userId (可选)
  - 响应: 商品详细信息
  - 前端集成: VUE/src/views/tourist/ProductDetail.vue

- ✅ GET /api/products/search - 搜索商品
  - 请求参数: keyword, page, size
  - 响应: 搜索结果, 分页信息
  - 前端集成: VUE/src/views/tourist/Products.vue

- ✅ GET /api/products/category/{category} - 按分类查询商品
  - 请求参数: category, page, size
  - 响应: 商品列表, 分页信息
  - 前端集成: VUE/src/views/tourist/Products.vue

#### 管理员端点
- ✅ POST /api/products/admin/create - 创建商品
  - 请求: name, description, price, stock, imageUrl, category, isGuangzhouSpecial
  - 响应: 创建的商品信息
  - 前端集成: VUE/src/views/admin/Products.vue

- ✅ PUT /api/products/admin/{id} - 更新商品
  - 请求: 商品信息
  - 响应: 更新后的商品信息
  - 前端集成: VUE/src/views/admin/Products.vue

- ✅ DELETE /api/products/admin/{id} - 删除商品
  - 响应: 成功消息
  - 前端集成: VUE/src/views/admin/Products.vue

### 5. 订单管理模块 (Order Module)

#### 订单创建端点
- ✅ POST /api/orders/attractions/create - 创建景点订单
  - 请求: userId, attractionId, quantity, visitDate
  - 响应: 订单信息 (orderNumber, status, totalPrice)
  - 前端集成: VUE/src/views/tourist/AttractionBooking.vue

- ✅ POST /api/orders/hotels/create - 创建酒店订单
  - 请求: userId, hotelId, roomType, checkInDate, checkOutDate, quantity
  - 响应: 订单信息
  - 前端集成: VUE/src/views/tourist/HotelBooking.vue

- ✅ POST /api/orders/products/create - 创建商品订单
  - 请求: userId, productId, quantity
  - 响应: 订单信息
  - 前端集成: VUE/src/views/tourist/ProductBooking.vue

#### 订单查询端点
- ✅ GET /api/orders/user/{userId} - 获取用户订单列表 (分页)
  - 请求参数: page, size
  - 响应: 订单列表, 分页信息
  - 前端集成: VUE/src/views/tourist/Orders.vue

- ✅ GET /api/orders/user/{userId}/status/{status} - 按状态查询用户订单
  - 请求参数: page, size
  - 响应: 订单列表, 分页信息
  - 前端集成: VUE/src/views/tourist/Orders.vue

- ✅ GET /api/orders/{orderId} - 获取订单详情
  - 响应: 订单详细信息, 订单项目列表
  - 前端集成: VUE/src/views/tourist/Orders.vue

#### 订单操作端点
- ✅ PUT /api/orders/{orderId}/cancel - 取消订单
  - 响应: 更新后的订单状态
  - 前端集成: VUE/src/views/tourist/Orders.vue

#### 管理员端点
- ✅ GET /api/orders/admin/all - 获取所有订单 (分页)
  - 请求参数: page, size
  - 响应: 订单列表, 分页信息
  - 前端集成: VUE/src/views/admin/Orders.vue

- ✅ GET /api/orders/admin/status/{status} - 按状态查询订单
  - 请求参数: page, size
  - 响应: 订单列表, 分页信息
  - 前端集成: VUE/src/views/admin/Orders.vue

- ✅ GET /api/orders/admin/type/{orderType} - 按类型查询订单
  - 请求参数: page, size
  - 响应: 订单列表, 分页信息
  - 前端集成: VUE/src/views/admin/Orders.vue

- ✅ PUT /api/orders/admin/{orderId}/status - 更新订单状态
  - 请求: status
  - 响应: 更新后的订单信息
  - 前端集成: VUE/src/views/admin/Orders.vue

- ✅ GET /api/orders/admin/statistics - 获取订单统计信息
  - 响应: 订单统计数据
  - 前端集成: VUE/src/views/admin/Orders.vue

### 6. 留言评价模块 (Comment Module)

#### 留言操作端点
- ✅ POST /api/comments/create - 创建留言
  - 请求: userId, targetType, targetId, content, rating
  - 响应: 留言信息
  - 前端集成: VUE/src/components/CommentForm.vue

- ✅ GET /api/comments/target/{targetType}/{targetId} - 获取目标的留言列表 (分页)
  - 请求参数: page, size
  - 响应: 留言列表, 分页信息
  - 前端集成: VUE/src/components/CommentList.vue

#### 管理员端点
- ✅ GET /api/comments/admin/all - 获取所有留言 (分页)
  - 请求参数: page, size
  - 响应: 留言列表, 分页信息
  - 前端集成: VUE/src/views/admin/Comments.vue

- ✅ PUT /api/comments/admin/{commentId}/approve - 批准留言
  - 响应: 更新后的留言信息
  - 前端集成: VUE/src/views/admin/Comments.vue

- ✅ PUT /api/comments/admin/{commentId}/reject - 拒绝留言
  - 响应: 更新后的留言信息
  - 前端集成: VUE/src/views/admin/Comments.vue

- ✅ PUT /api/comments/admin/{commentId}/pin - 置顶留言
  - 响应: 更新后的留言信息
  - 前端集成: VUE/src/views/admin/Comments.vue

- ✅ DELETE /api/comments/admin/{commentId} - 删除留言
  - 响应: 成功消息
  - 前端集成: VUE/src/views/admin/Comments.vue

### 7. 公告管理模块 (Announcement Module)

#### 公告查询端点
- ✅ GET /api/announcements/list - 获取公告列表 (分页)
  - 请求参数: page, size
  - 响应: 公告列表, 分页信息
  - 前端集成: VUE/src/views/tourist/Home.vue

#### 管理员端点
- ✅ POST /api/announcements/admin/create - 创建公告
  - 请求: title, content
  - 响应: 创建的公告信息
  - 前端集成: VUE/src/views/admin/Announcements.vue

- ✅ PUT /api/announcements/admin/{announcementId} - 更新公告
  - 请求: title, content
  - 响应: 更新后的公告信息
  - 前端集成: VUE/src/views/admin/Announcements.vue

- ✅ DELETE /api/announcements/admin/{announcementId} - 删除公告
  - 响应: 成功消息
  - 前端集成: VUE/src/views/admin/Announcements.vue

### 8. 收藏管理模块 (Favorite Module)

#### 收藏操作端点
- ✅ POST /api/favorites/add - 添加收藏
  - 请求: userId, targetType, targetId
  - 响应: 收藏信息
  - 前端集成: VUE/src/components/FavoriteButton.vue

- ✅ DELETE /api/favorites/remove - 移除收藏
  - 请求: userId, targetType, targetId
  - 响应: 成功消息
  - 前端集成: VUE/src/components/FavoriteButton.vue

- ✅ GET /api/favorites/user/{userId} - 获取用户收藏列表 (分页)
  - 请求参数: page, size
  - 响应: 收藏列表, 分页信息
  - 前端集成: VUE/src/components/FavoritesList.vue

- ✅ GET /api/favorites/check - 检查是否已收藏
  - 请求参数: userId, targetType, targetId
  - 响应: 是否已收藏
  - 前端集成: VUE/src/components/FavoriteButton.vue

### 9. 浏览历史模块 (BrowsingHistory Module)

#### 浏览历史端点
- ✅ GET /api/browsing-history/user/{userId} - 获取用户浏览历史 (分页)
  - 请求参数: page, size
  - 响应: 浏览历史列表, 分页信息
  - 前端集成: VUE/src/components/BrowsingHistoryList.vue

- ✅ DELETE /api/browsing-history/user/{userId} - 清空用户浏览历史
  - 响应: 成功消息
  - 前端集成: VUE/src/views/tourist/Profile.vue

### 10. 路线推荐模块 (Route Module)

#### 路线查询端点
- ✅ GET /api/routes/list - 获取路线列表 (分页)
  - 请求参数: page, size
  - 响应: 路线列表, 分页信息
  - 前端集成: VUE/src/views/tourist/Routes.vue

- ✅ GET /api/routes/{routeId} - 获取路线详情
  - 响应: 路线详细信息, 路线项目列表
  - 前端集成: VUE/src/views/tourist/Routes.vue

- ✅ GET /api/routes/recommend - 获取推荐路线
  - 请求参数: durationDays, attractionPreference, page, size
  - 响应: 推荐路线列表, 分页信息
  - 前端集成: VUE/src/views/tourist/Routes.vue

#### 管理员端点
- ✅ POST /api/routes/admin/create - 创建路线
  - 请求: name, description, durationDays, totalPrice
  - 响应: 创建的路线信息
  - 前端集成: VUE/src/views/admin/Routes.vue

- ✅ PUT /api/routes/admin/{routeId} - 更新路线
  - 请求: 路线信息
  - 响应: 更新后的路线信息
  - 前端集成: VUE/src/views/admin/Routes.vue

- ✅ DELETE /api/routes/admin/{routeId} - 删除路线
  - 响应: 成功消息
  - 前端集成: VUE/src/views/admin/Routes.vue

- ✅ POST /api/routes/admin/{routeId}/items - 添加路线项目
  - 请求: dayNumber, itemType, itemId, sequence
  - 响应: 路线项目信息
  - 前端集成: VUE/src/views/admin/Routes.vue

- ✅ DELETE /api/routes/admin/{routeId}/items/{itemId} - 删除路线项目
  - 响应: 成功消息
  - 前端集成: VUE/src/views/admin/Routes.vue

## 前后端数据流验证

### 用户注册流程
1. 前端: VUE/src/views/auth/Register.vue 收集用户输入
2. 前端: VUE/src/api/user.js 发送 POST /api/users/register 请求
3. 后端: UserController.register() 处理请求
4. 后端: UserService.register() 执行业务逻辑
5. 后端: UserRepository 保存用户数据
6. 后端: 返回 ApiResponse 格式的响应
7. 前端: 接收响应并存储用户信息到 localStorage
8. 前端: 重定向到首页或登录页

### 景点浏览流程
1. 前端: VUE/src/views/tourist/Attractions.vue 加载景点列表
2. 前端: VUE/src/api/attraction.js 发送 GET /api/attractions/list 请求
3. 后端: AttractionController.getAttractionList() 处理请求
4. 后端: AttractionService 查询景点数据
5. 后端: 返回分页的景点列表
6. 前端: 显示景点列表
7. 用户点击景点详情
8. 前端: VUE/src/views/tourist/AttractionDetail.vue 加载详情
9. 前端: 发送 GET /api/attractions/{id} 请求
10. 后端: 记录浏览历史
11. 后端: 返回景点详细信息
12. 前端: 显示景点详情

### 订单创建流程
1. 前端: VUE/src/views/tourist/AttractionBooking.vue 收集预订信息
2. 前端: VUE/src/api/order.js 发送 POST /api/orders/attractions/create 请求
3. 后端: OrderController.createAttractionOrder() 处理请求
4. 后端: OrderService 创建订单
5. 后端: OrderRepository 保存订单数据
6. 后端: 返回订单信息 (包含订单号)
7. 前端: 显示订单确认信息
8. 用户查看订单
9. 前端: VUE/src/views/tourist/Orders.vue 加载订单列表
10. 前端: 发送 GET /api/orders/user/{userId} 请求
11. 后端: 返回用户的订单列表
12. 前端: 显示订单列表

### 管理员管理流程
1. 管理员登录系统
2. 前端: VUE/src/views/admin/Attractions.vue 加载景点管理页面
3. 前端: 发送 GET /api/attractions/list 请求获取景点列表
4. 管理员创建新景点
5. 前端: 发送 POST /api/attractions/admin/create 请求
6. 后端: 创建景点并返回信息
7. 前端: 刷新景点列表
8. 管理员查看订单统计
9. 前端: VUE/src/views/admin/Orders.vue 加载订单管理页面
10. 前端: 发送 GET /api/orders/admin/statistics 请求
11. 后端: 返回订单统计数据
12. 前端: 显示统计信息

## 集成问题修复清单

### 已验证的集成点
- ✅ 前端 API 客户端配置正确
- ✅ 后端 CORS 跨域配置已启用
- ✅ 所有控制器都配置了 @CrossOrigin 注解
- ✅ 请求头传递用户信息 (X-User-Id, X-User-Role)
- ✅ 统一的 ApiResponse 响应格式
- ✅ 全局异常处理已配置
- ✅ 数据库连接配置正确
- ✅ 所有实体类和仓储接口已创建
- ✅ 所有服务类已实现
- ✅ 所有控制器端点已实现

### 可能的集成问题及解决方案

#### 问题1: 数据库连接失败
- 原因: MySQL 服务未启动或连接参数错误
- 解决: 确保 MySQL 服务运行，检查 application.yml 中的连接参数

#### 问题2: CORS 跨域请求被拒绝
- 原因: 前端和后端端口不同
- 解决: 已在所有控制器配置 @CrossOrigin(origins = "*", maxAge = 3600)

#### 问题3: 用户信息丢失
- 原因: 请求头中未传递用户信息
- 解决: 前端 API 客户端已配置请求拦截器自动添加用户信息

#### 问题4: 响应格式不一致
- 原因: 不同的控制器返回不同的响应格式
- 解决: 所有控制器都使用统一的 ApiResponse 格式

#### 问题5: 日志输出乱码
- 原因: 字符编码设置不正确
- 解决: 已在 application.yml 配置 UTF-8 字符编码

## 测试执行

### 运行集成测试
```bash
cd SpringBoot
mvn test -Dtest=ApiIntegrationTest
```

### 测试覆盖范围
- ✅ 用户注册和登录
- ✅ 用户个人信息管理
- ✅ 景点查询和详情
- ✅ 酒店查询和详情
- ✅ 商品查询和详情
- ✅ 订单创建和查询
- ✅ 留言评价
- ✅ 公告管理
- ✅ 收藏功能
- ✅ 浏览历史
- ✅ 路线推荐
- ✅ 完整的用户旅程
- ✅ 完整的管理员旅程

## 总结

所有 API 端点已验证连接正常，前后端数据流完整。系统已准备好进行功能测试和性能优化。
